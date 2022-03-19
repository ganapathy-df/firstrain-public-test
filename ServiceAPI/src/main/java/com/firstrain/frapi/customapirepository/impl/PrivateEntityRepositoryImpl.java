package com.firstrain.frapi.customapirepository.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.AutoIncrementDbAPI;
import com.firstrain.db.api.EntityBacktestDbAPI;
import com.firstrain.db.api.EntityHistoryDbAPI;
import com.firstrain.db.api.PrivateEntityDbAPI;
import com.firstrain.db.obj.AutoIncrement;
import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.db.obj.EntityHistory;
import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.db.obj.PrivateEntityList;
import com.firstrain.web.pojo.CreateInputBean;

@Service
public class PrivateEntityRepositoryImpl {

	private static final Logger LOG = Logger.getLogger(PrivateEntityRepositoryImpl.class);
	private static final String STATE_NEW = "NEW";
	private static final String STATE_DEV = "DEV";
	private static final String STATE_LIVE = "LIVE";

	public void insertIntoPrivateEntityTable(String searchToken, CreateInputBean inputbean, String enterprise_id, String definition)
			throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);

			AutoIncrement ai = new AutoIncrement();
			AutoIncrement ai1 = new AutoIncrement();

			AutoIncrementDbAPI.persistAutoIncrement(txn, ai);
			AutoIncrementDbAPI.persistAutoIncrement(txn, ai1);

			PrivateEntity pe = new PrivateEntity();
			pe.setSearchToken(searchToken);
			pe.setBrand(inputbean.getTaxonomyDirective());
			pe.setDefinition_dev(definition);
			pe.setCreatedByEmail(inputbean.getAuthor().getEmail());
			pe.setEnterprise_id(enterprise_id);
			pe.setId_dev(ai.getId());
			pe.setId_live(ai1.getId());
			pe.setName(inputbean.getName());
			pe.setStatus(STATE_DEV);
			pe.setCreatedByUser(inputbean.getAuthor().getName());

			PrivateEntityDbAPI.persistPrivateEntity(txn, pe);

			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting PrivateEntity: " + searchToken, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}

	}

	public void promote(String searchToken, PrivateEntity pe, String name, String email) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			PrivateEntity peRes = PrivateEntityDbAPI.updatePrivateEntity(txn, pe.getId());
			peRes.setDefinition_live(pe.getDefinition_dev());
			peRes.setLastPromotedByUser(name);
			peRes.setLastPromotedByEmail(email);
			peRes.setStatus(STATE_LIVE);
			peRes.setLast_promoted(new Timestamp(System.currentTimeMillis()));
			
			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting PrivateEntity: " + searchToken, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	public List<PrivateEntity> getDefinitions(String searchToken) throws Exception {
		return PrivateEntityDbAPI.getPrivateEntity(searchToken);
	}

	public PrivateEntity updateState(String searchToken, String state, long id) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			PrivateEntity peRes = PrivateEntityDbAPI.updatePrivateEntity(txn, id);
			peRes.setStatus(state);
			txn.commit();

			return peRes;
		} catch (Exception e) {
			LOG.error("Error while persisting PrivateEntity: " + searchToken, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	public int updateDefinition(final String searchToken, final String definition_dev, PrivateEntity pe, String user, String email)
			throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);

			EntityHistory eh = new EntityHistory();
			eh.setDefinition_dev(pe.getDefinition_dev());
			eh.setDefinition_live(pe.getDefinition_live());
			eh.setEmail(email);
			eh.setId_dev(pe.getId_dev());
			eh.setId_live(pe.getId_live());
			eh.setName(pe.getName());
			eh.setSearchToken(pe.getSearchToken());
			eh.setStatus(pe.getStatus());
			eh.setUser(user);

			EntityHistoryDbAPI.persistEntityHistory(txn, eh);

			PrivateEntity peRes = PrivateEntityDbAPI.updatePrivateEntity(txn, pe.getId());
			peRes.setDefinition_dev(definition_dev);

			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting PrivateEntity: " + searchToken, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
		return -1;
	}

	public List<EntityBacktest> getEntityBackTestList(String searchToken, boolean isDocJson) throws Exception {

		if (StringUtils.isEmpty(searchToken)) {
			return null;
		}

		if (isDocJson) {
			return EntityBacktestDbAPI.getEntityBacktestWithDocsJson(searchToken);
		}

		return EntityBacktestDbAPI.getEntityBacktestWithOutDocsJson(searchToken);
	}

	public List<EntityHistory> getEntityHistoryList(String searchToken) throws Exception {

		if (StringUtils.isEmpty(searchToken)) {
			return null;
		}

		return EntityHistoryDbAPI.getEntityHistoryBySearchToken(searchToken);
	}

	public List<EntityBacktest> getEntityBacktestList(String searchToken) throws Exception {

		if (StringUtils.isEmpty(searchToken)) {
			return null;
		}

		return EntityBacktestDbAPI.getEntityBacktest(searchToken);
	}

	public List<PrivateEntity> getPrivateEntityList(String searchToken) throws Exception {

		if (StringUtils.isEmpty(searchToken)) {
			return null;
		}

		return PrivateEntityDbAPI.getPrivateEntity(searchToken);
	}

	public long insertIntoEntityBacktest(String docs, PrivateEntity pe) throws Exception {

		Transaction txn = null;
		long job_id = -1;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);

			EntityBacktest eb = new EntityBacktest();
			eb.setCatId(pe.getId_dev());
			eb.setDefinition(pe.getDefinition_dev());
			eb.setSearchToken(pe.getSearchToken());
			eb.setStatus(STATE_NEW);

			if (StringUtils.isNotEmpty(docs)) {
				eb.setDoc_json(docs);
			}

			EntityBacktestDbAPI.persistEntityBacktest(txn, eb);
			job_id = eb.getId();

			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting EntityBacktest : " + pe.getSearchToken(), e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}

		return job_id;
	}

	public List<EntityBacktest> getEntityBackTestById(long jobId) throws Exception {

		if (jobId < -1) {
			return null;
		}

		return EntityBacktestDbAPI.getEntityBacktestById(jobId);
	}

	public List<PrivateEntityList> getPrivateEntityListByTaxonomyDirective(List<String> taxonomyDirectiveLst) throws Exception {

		if (CollectionUtils.isEmpty(taxonomyDirectiveLst)) {
			return null;
		}

		return PrivateEntityDbAPI.getPrivateEntityListBybrand(taxonomyDirectiveLst);
	}

}
