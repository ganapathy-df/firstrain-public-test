package com.firstrain.frapi.customapirepository.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.obj.EntityBacktest;

@Service
public class EntityBackTestRepositoryImpl {

	private static final Logger LOG = Logger.getLogger(EntityBackTestRepositoryImpl.class);

	public EntityBacktest updateState(String state, long id) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			EntityBacktest eRes = updateEntityBackTest(txn, id);
			eRes.setStatus(state);
			txn.commit();

			return eRes;
		} catch (Exception e) {
			LOG.error("Error while persisting EntityBackTest Job: " + id, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	private EntityBacktest updateEntityBackTest(Transaction txn, long id) {
		return txn.fetch(id, EntityBacktest.class);
	}
}
