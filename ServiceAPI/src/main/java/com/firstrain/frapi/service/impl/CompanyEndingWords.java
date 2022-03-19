package com.firstrain.frapi.service.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.firstrain.frapi.util.CompanyEndingLoader;
import com.firstrain.log4j.Log4jUtils;
import com.firstrain.utils.FR_Loader;

@Service
public class CompanyEndingWords {
	private final Logger LOG = Logger.getLogger(CompanyEndingWords.class);
	private Set<String> companyEndingWords;
	private Set<String> companyEndingWordsRegex;
	private final String st_THE_Regex = "^(?i)the\\s+[,'. ]*";

	public Set<String> getCompanyEndingWords() {
		if (companyEndingWords == null) {
			load();
		}
		return companyEndingWords;
	}

	public Set<String> getCompanyEndingWordsRegex() {
		if (companyEndingWordsRegex == null) {
			load();
		}
		return companyEndingWordsRegex;
	}

	private void load() {
		Set<String> words = new LinkedHashSet<String>();
		Set<String> wordsRegex = new LinkedHashSet<String>();
        try {
            InputStream in = FR_Loader.getResourceAsStream("resource/endings.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            CompanyEndingLoader.loadCompanyEnding(words, wordsRegex, documentBuilderFactory, in);
        } catch (RuntimeException e) {
            LOG.error("Error while loading CompanyEndingWords form resource/endings.xml", e);
        }
		companyEndingWords = Collections.unmodifiableSet(words);
		companyEndingWordsRegex = Collections.unmodifiableSet(wordsRegex);
	}

	public String trimCompanyEndingWord(String sParam) {
		String s = sParam;
		if (s != null && !s.isEmpty()) {
			for (String regex : this.getCompanyEndingWordsRegex()) {
				String label1 = s.replaceAll(regex, "");
				if (label1 != s) {
					s = label1;
					break;
				}
			}
			s = s.replaceAll(st_THE_Regex, "");
		}
		return s;
	}

	public static void main(String[] args) {
		Log4jUtils.initializeConsoleLogger(Level.DEBUG);
		String[] labels = {"the Siemens AG", "Novartis AG", "The ,Apple,Inc", "tHE Internations Business Machine Corporation",
				"thE ,'Microsoft Dynamics", "The Dow Chemical Company", "THERMOGENESIS CORP.", "The Furukawa Electric Co., Ltd.",
				"TheMicrosoftCorporation", "JPMorgan Chase & Co.", "JPMorgan Chase and Co."};

		for (String label : labels) {
			// System.out.println(trimCompanyEndingWord(label));
		}
	}

}
