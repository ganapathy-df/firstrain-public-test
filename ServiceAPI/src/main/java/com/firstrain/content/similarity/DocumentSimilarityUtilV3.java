package com.firstrain.content.similarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.firstrain.content.similarity.measures.CosineSimilarity;
import com.firstrain.content.similarity.measures.DiceCoefficientStrategy;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SearchSpec;

public class DocumentSimilarityUtilV3 {

	private static final Logger LOG = Logger.getLogger(DocumentSimilarityUtilV3.class);

	private double summarySimilarityThreshold = 0.20;
	private int nGramSize = 7;
	private double titleSimilarityThreshold = 0.75;
	private int noOfDays = 5;

	private Set<String> stopWords = new HashSet<String>();
	private final DiceCoefficientStrategy diceMeasure = new DiceCoefficientStrategy();
	private CosineSimilarity algoCosineMeasure = new CosineSimilarity();

	public DocumentSimilarityUtilV3(String stopWordsFilePath) throws Exception {
		setStopWordsFilePath(stopWordsFilePath);
	}

	/**
	 * API to process list of documents for similarity
	 * 
	 * @param siteDocList
	 * @return List<FR_ISiteDocument>
	 * @throws Throwable
	 */

	public List<DocEntry> processDocument(List<DocEntry> siteDocList) throws Exception {
		if (siteDocList == null || siteDocList.isEmpty()) {
			return null;
		} else if (siteDocList.size() == 1) {
			return siteDocList; // i am similar to myself.
		}

		Set<String> t = new TreeSet<String>();
		long tm = System.currentTimeMillis();
		List<DocEntry> finalList = new ArrayList<DocEntry>();
		DocEntry doc1 = null, doc2 = null;
		int comp = 0, removed = 0, size = siteDocList.size();
		int i = 0;
		do {
			doc1 = siteDocList.get(i++);
			t.add(doc1.getTitle());
			comp++;
			if (i < siteDocList.size()) {
				Iterator<DocEntry> iter2 = siteDocList.listIterator(i);
				while (iter2.hasNext()) {
					comp++;
					doc2 = (DocEntry) iter2.next();
					if (similarDocs(doc1, doc2)) {
						if (LOG.isTraceEnabled()) {
							LOG.trace(String.format("d1: %s\nd2: %s\n\n s1:%s\n\n s2:%s", doc1.getTitle(), doc2.getTitle(),
									doc1.getSummary(), doc2.getSummary()));
						}

						iter2.remove();
						removed++;
					}
				}
			}

			finalList.add(doc1);
		} while (i < siteDocList.size());

		LOG.info(String.format("Similar total comparisons : %d, for size: %d, removed : %d, time : %d ms", comp, size, removed,
				(System.currentTimeMillis() - tm)));
		return siteDocList;
	}

	private boolean similarDocs(DocEntry doc1, DocEntry doc2) {
		long g1 = doc1.groupId;
		long g2 = doc2.groupId;
		if (g1 == g2 && g1 > 0 && g2 > 0) {
			if (LOG.isTraceEnabled()) {
				LOG.trace(String.format("Same Group Id : %d, doc1: %d, doc2: %d", g1, doc1.sitedocId, doc2.sitedocId));
			}

			return true;
		}

		if (insertTimeDiffThresholdReached(doc1, doc2)) {
			if (LOG.isTraceEnabled()) {
				LOG.trace(String.format("Documents insertime differ by more than %s days, doc1: %d, doc2: %d", noOfDays, doc1.sitedocId,
						doc2.sitedocId));
			}

			return false;
		}

		return isDocSimilarAddedHueristics(doc1, doc2);
	}

	private boolean insertTimeDiffThresholdReached(final DocEntry doc1, final DocEntry doc2) {
		Date t = doc1.insertTime;
		long t1 = t == null ? 0 : t.getTime();

		t = doc2.insertTime;
		long t2 = t == null ? 0 : t.getTime();
		long diff = Math.abs(t1 - t2);
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

		return days >= noOfDays;
	}

	private boolean eqOrSubSequence(String s1, String s2) {
		int l1 = s1.length();
		int l2 = s2.length();
		// short circuit the length comparison before making text sequence check.
		if (l1 == l2 && s1.equals(s2)) {
			return true;
		} else if (l1 < l2 && s2.contains(s1)) {
			return true;
		} else if (l2 < l1 && s1.contains(s2)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDocSimilarAddedHueristics(DocEntry doc1, DocEntry doc2) {
		// LOG.info("Comparing :" + doc1.sitedocId +"," +doc2.sitedocId);
		String s1 = doc1.getSummary();
		String s2 = doc2.getSummary();

		if (StringUtils.isBlank(s1) || StringUtils.isBlank(s2)) {
			return false;
		}

		boolean hascompany = false;
		boolean goodcompanymatch = false;
		boolean goodtopicmatch = false;
		if (CollectionUtils.isNotEmpty(doc1.matchedCompanies) && CollectionUtils.isNotEmpty(doc2.matchedCompanies)) {
			int companiesCount = 0, companiesMatchCount = 0;
			boolean mustBeTrue = true;
			for (DocCatEntry c1 : doc1.matchedCompanies) {
				if (c1.band == SearchSpec.SCOPE_BROAD) {
					continue;
				}

				companiesCount++;
				String id1 = c1.entity.id;
				boolean matched = false;
				for (DocCatEntry c2 : doc2.matchedCompanies) {
					if (id1.equals(c2.entity.id) && (c1.band <= c2.band)) {
						companiesMatchCount++;
						if (c1.band == SearchSpec.SCOPE_NARROW) {
							matched = true;
						}
						break;
					}
				}

				if (c1.band == SearchSpec.SCOPE_NARROW) {
					if (matched) {
						goodcompanymatch = true;
					} else {
						goodcompanymatch = false;
						mustBeTrue = false;
						break;
					}
				}
			}

			hascompany = companiesCount > 0;
			if (!goodcompanymatch && mustBeTrue) {
				if (companiesMatchCount >= .5 * companiesCount) {
					goodcompanymatch = true;
				}
			}

			// if the doc has 2 companies and none matched with the doc in its said scope, let us ommit the comparison
			if (hascompany && companiesCount <= 2 && !goodcompanymatch) {
				return false;
			}
		}

		if (CollectionUtils.isNotEmpty(doc1.matchedTopics) && CollectionUtils.isNotEmpty(doc2.matchedTopics)) {
			int topicsCount = 0, topicsMatchCount = 0;
			boolean mustBeTrue = true;
			for (DocCatEntry t1 : doc1.matchedTopics) {
				if (t1.band == SearchSpec.SCOPE_BROAD) {
					continue;
				}

				topicsCount++;
				String id1 = t1.entity.id;
				boolean matched = false;
				for (DocCatEntry t2 : doc2.matchedTopics) {
					if (id1.equals(t2.entity.id) && (t1.band <= t2.band)) {
						topicsMatchCount++;
						if (t1.band == SearchSpec.SCOPE_NARROW) {
							matched = true;
						}
						break;
					}
				}

				if (t1.band == SearchSpec.SCOPE_NARROW) {
					if (matched) {
						goodtopicmatch = true;
					} else {
						goodtopicmatch = false;
						mustBeTrue = false;
					}
				}
			}

			if (!goodtopicmatch && mustBeTrue) {
				if (hascompany) {
					if (goodcompanymatch && (topicsMatchCount >= .5 * topicsCount)) {
						goodtopicmatch = true;
					}
				} else {
					if (topicsCount <= 2) {
						if (topicsMatchCount == topicsCount) {
							goodtopicmatch = true;
						}
					} else if (topicsMatchCount >= 2 && topicsMatchCount >= .7 * topicsCount) {
						goodtopicmatch = true;
					}
				}
			}
		}

		// if no topic and companies match in the good scope its hardly a duplicate match.
		if (((hascompany && !goodcompanymatch) || !hascompany) && !goodtopicmatch) {
			return false;
		}

		if (eqOrSubSequence(s1, s2)) {
			logData("Summary : equal or subsequence : %d, %d \ns1: %s \ns2: %s\n\n", doc1, doc2);

			return true;
		}


		double summarySimilarity = diceMeasure.score(s1, s2, nGramSize);
		s1 = s2 = null;

		boolean summarySimilar = false, titleSimilar = false;
		if (summarySimilarity > 0.95 && doc1.sourceEntity.id.equals(doc2.sourceEntity.id)) {
			// ignore same source exact duplicate summary bug in our system
			LOG.trace("Same source same summary:" + doc1.sitedocId + "," + doc2.sitedocId + " - " + summarySimilarity);
		} else {
			summarySimilar = summarySimilarity > summarySimilarityThreshold;
		}

		if (summarySimilar) {
			logData("Summary : %s, %s \ns1: %s \ns2: %s\n\n", doc1, doc2);

			return true;
		} else {
			if (StringUtils.isBlank(doc1.getTitle()) || StringUtils.isBlank(doc2.getTitle())) {
				return false;
			} else if (goodcompanymatch && goodtopicmatch) {

				String t1 = doc1.getTitle().toLowerCase();
				String t2 = doc2.getTitle().toLowerCase();

				if (eqOrSubSequence(t1, t2)) {
					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Title : equal or subsequence : %d, %d \nt1: %s \nt2: %s\n\n", doc1.sitedocId,
								doc2.sitedocId, doc1.getTitle(), doc2.getTitle()));
					}

					return true;
				}

				double titleSimilarityDice = diceMeasure.score(t1, t2, 4);
				double titleSimilarityCosine = getCosineSimilarity(t1, t2);
				double titleSimilarity = (titleSimilarityDice + titleSimilarityCosine) / 2; // average of 2 measures
				titleSimilar = titleSimilarity >= titleSimilarityThreshold;

				if (LOG.isTraceEnabled() && titleSimilar) {
					LOG.trace(String.format("Title : %s, %s \nt1: %s \nt2: %s\n\n", doc1.sitedocId, doc2.sitedocId, doc1.getTitle(),
							doc2.getTitle()));
				}

				return titleSimilar;
			}
		}

		return false;
	}

	private void logData(final String message, final DocEntry doc1, final DocEntry doc2) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format(message, doc1.sitedocId, doc2.sitedocId, doc1.getSummary(),
					doc2.getSummary()));
		}
	}

	private static final String regex = "\\s+";

	protected double getCosineSimilarity(String s1, String s2) {
		try {
			List<String> st1 = new ArrayList<String>(Arrays.asList(s1.split(regex)));
			List<String> st2 = new ArrayList<String>(Arrays.asList(s2.split(regex)));

			st1.removeAll(stopWords);
			st2.removeAll(stopWords);

			return algoCosineMeasure.getSimilarity(st1, st2);
		} catch (Exception e) {
			LOG.error("Error for cosine similarity:", e);
		}

		return 0;

	}

	public void setnGramSize(int nGramSize) {
		this.nGramSize = nGramSize;
	}

	public void setTitleSimilarityThreshold(double titleSimilarityThreshold) {
		this.titleSimilarityThreshold = titleSimilarityThreshold;
	}

	public void setStopWordsFilePath(String stopWordsFilePath) {
		if (stopWordsFilePath != null) {
			File file = new File(stopWordsFilePath);
			if (!file.exists()) {
				LOG.warn(String.format(
						"The stop words file path : %s , Does not exists. "
								+ "The system would ignore the stop words and perform similarity checks without excluding stopwords.",
						stopWordsFilePath), new FileNotFoundException(stopWordsFilePath));
			} else {
				List<String> words;
				try {
					words = FileUtils.readLines(file);
					stopWords.clear();
					stopWords.addAll(words);
					LOG.info("using the stopword file :" + stopWordsFilePath + ", for summary similarity cleanup..");
				} catch (IOException ignored) {
				}
			}
		}
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}
}
