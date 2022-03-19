package com.firstrain.frapi.customapirepository.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.DocCategorizeFeedbackDbAPI;
import com.firstrain.db.api.DocCategorizeResponseHistoryDbAPI;
import com.firstrain.db.obj.DocCategorizeFeedback;
import com.firstrain.db.obj.DocCategorizeResponseHistory;

@Service
public class DocCategorizeRepositoryImpl {

	private static final Logger LOG = Logger.getLogger(DocCategorizeRepositoryImpl.class);

	public void insertIntoDocCategorizeResponseHistory(final Long docId, final String taxonomyDirective, final String serviceRes,
			final String actualRes) throws Exception {

		DocCategorizeResponseHistory docCategorizeResponseHistory = new DocCategorizeResponseHistory();
		docCategorizeResponseHistory.setDocId(docId);
		docCategorizeResponseHistory.setTaxonomyDirective(taxonomyDirective);
		docCategorizeResponseHistory.setFrCategorizeServiceResponse(serviceRes);
		docCategorizeResponseHistory.setActualResponse(actualRes);

		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			DocCategorizeResponseHistoryDbAPI.persistDocCategorizeResponseHistory(txn, docCategorizeResponseHistory);
			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting DocCategorizeResponseHistory: " + docId, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	public void insertIntoDocCategorizeFeedback(final String feedback) throws Exception {

		DocCategorizeFeedback docCategorizeFeedback = new DocCategorizeFeedback();
		docCategorizeFeedback.setFeedback(feedback);

		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			DocCategorizeFeedbackDbAPI.persistDocCategorizeFeedback(txn, docCategorizeFeedback);
			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting DocCategorizeFeedback: ", e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

}
