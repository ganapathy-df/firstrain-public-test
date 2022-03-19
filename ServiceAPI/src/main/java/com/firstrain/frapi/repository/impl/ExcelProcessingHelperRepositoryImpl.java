package com.firstrain.frapi.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.util.CompanyEndingLoader;
import com.firstrain.utils.FR_Loader;

@Repository
public class ExcelProcessingHelperRepositoryImpl implements ExcelProcessingHelperRepository {

	private final Logger LOG = Logger.getLogger(ExcelProcessingHelperRepositoryImpl.class);
	private final String EXCEL_FOLDER_PATH = "resource/";
	private final String COMPANY_ENDING = "endings.xml";
	private static final String PLATINUM_SOURCES = "PlatinumSourcesV1.txt";

	private Set<String> companyEndingWords;
	private Set<String> companyEndingWordsRegex;
	private AtomicLong companyEndingsLastModifiedTime = new AtomicLong(-1L);
	private AtomicLong platinumSourcesLastModifiedTime = new AtomicLong(-1L);

	private Map<Integer, Integer> platinumSourceVsRank = new HashMap<Integer, Integer>();

	@PostConstruct
	public void init() throws ServiceException, IOException {
		InputStream iStream = getFileFromPath(COMPANY_ENDING, companyEndingsLastModifiedTime);
		if (iStream != null) {
			loadCompanyEnding(iStream);
			iStream.close();
		}

		iStream = getFileFromPath(PLATINUM_SOURCES, platinumSourcesLastModifiedTime);
		if (iStream != null) {
			loadPlatinumSources(iStream);
			iStream.close();
		}
	}

	public void loadCompanyEnding(InputStream istream) {
		Set<String> words = new LinkedHashSet<String>();
		Set<String> wordsRegex = new LinkedHashSet<String>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        CompanyEndingLoader.loadCompanyEnding(words, wordsRegex, documentBuilderFactory, istream);
		setCompanyEndingWords(Collections.unmodifiableSet(words));
		setCompanyEndingWordsRegex(Collections.unmodifiableSet(wordsRegex));
	}

	@Override
	public Set<String> getCompanyEndingWords() {
		return companyEndingWords;
	}

	public void setCompanyEndingWords(Set<String> companyEndingWords) {
		this.companyEndingWords = companyEndingWords;
	}

	@Override
	public Set<String> getCompanyEndingWordsRegex() {
		return companyEndingWordsRegex;
	}

	public void setCompanyEndingWordsRegex(Set<String> companyEndingWordsRegex) {
		this.companyEndingWordsRegex = companyEndingWordsRegex;
	}

	@Override
	public Map<Integer, Integer> getPaltinumSourceVsRank() {
		return platinumSourceVsRank;
	}

	private InputStream getFileFromPath(String fileName, AtomicLong lastModifiedTime) throws IOException {
		URL resource = FR_Loader.getResource(EXCEL_FOLDER_PATH + fileName);
		if (resource == null) {
			LOG.warn("Required file " + fileName + " is not present in deployed (resource) folder");
			return null;
		}
		File f = new File(resource.getPath());
		if (lastModifiedTime.get() != f.lastModified()) {
			LOG.info("File has been modified so going to reload " + fileName);
			lastModifiedTime.set(f.lastModified());
			InputStream iStream = resource.openStream();
			return iStream;
		} else {
			return null;
		}
	}


	private void loadPlatinumSources(InputStream iStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
			platinumSourceVsRank.clear();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				platinumSourceVsRank.put(Integer.valueOf(arr[1]), Integer.valueOf(arr[0]));
			}
			LOG.info("Loaded <" + platinumSourceVsRank.size() + "> platinum sources from: " + platinumSourceVsRank);
		} catch (Exception e) {
			LOG.error("Error while reading file: " + platinumSourceVsRank, e);
		}

	}
}
