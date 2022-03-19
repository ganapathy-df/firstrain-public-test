package com.firstrain.web.service.core;

import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.db.obj.EntityHistory;
import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.db.obj.PrivateEntityList;
import com.firstrain.frapi.customapirepository.impl.PrivateEntityRepositoryImpl;
import com.firstrain.web.domain.UpdateDefinition;
import com.firstrain.web.exception.CustomExceptionError;
import com.firstrain.web.exception.CustomExceptionSuccess;
import com.firstrain.web.pojo.CreateInputBean;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;

@Service
public class PrivateEntityService {

	private static final Logger LOG = Logger.getLogger(PrivateEntityService.class);
	private static final String STATE_DEV = "DEV";
	private static final String STATE_LIVE = "LIVE";
	private static final String STATE_ACTIVE = "ACTIVE";
	private static final String STATE_INACTIVE = "INACTIVE";
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
	private PrivateEntityRepositoryImpl privateEntityRepositoryImpl;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private SearchTokenUtil searchTokenUtil;

	public long backTestSubmit(String searchToken, String docs) throws Exception {

		Future<List<EntityBacktest>> entityBacktestLstF = taskExecutor.submit(getEntityBackTestList(searchToken, docs));
		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));

		List<EntityBacktest> entityBacktestLst = entityBacktestLstF.get();

		if (CollectionUtils.isNotEmpty(entityBacktestLst)) {
			if (StringUtils.isEmpty(docs)) {
				return entityBacktestLst.get(0).getId();
			} else {
				for (EntityBacktest entityBacktest : entityBacktestLst) {
					if (docs.equals(entityBacktest.getDoc_json())) {
						return entityBacktest.getId();
					}
				}
			}
		}

		List<PrivateEntity> privateEntities = getAndValidateList(privateEntityLstF);

		PrivateEntity pe = privateEntities.get(0);

		checkStatusInactive(pe); 

		long job_id = privateEntityRepositoryImpl.insertIntoEntityBacktest(docs, pe);

		if (job_id < 0) {
			throw new CustomExceptionSuccess("Some error occured while generating job_id for searchToken : " + searchToken, 500);
		}

		return job_id;
	}

	public EntityBacktest backTestCheck(long jobId) throws Exception {

		List<EntityBacktest> entityBacktestLst = privateEntityRepositoryImpl.getEntityBackTestById(jobId);

		if (CollectionUtils.isEmpty(entityBacktestLst)) {
			throw new CustomExceptionError(118); // entity already exists;
		}

		return entityBacktestLst.get(0);
	}

	private Callable<List<EntityBacktest>> getEntityBackTestList(final String searchToken, final String docs) {

		return new Callable<List<EntityBacktest>>() {
			@Override
			public List<EntityBacktest> call() throws Exception {
				return privateEntityRepositoryImpl.getEntityBackTestList(searchToken, StringUtils.isNotEmpty(docs) ? true : false);
			}
		};

	}

	private Callable<List<PrivateEntity>> getPrivateEntityList(final String searchToken) {

		return new Callable<List<PrivateEntity>>() {
			@Override
			public List<PrivateEntity> call() throws Exception {
				return privateEntityRepositoryImpl.getPrivateEntityList(searchToken);
			}
		};

	}

	private Callable<List<EntityHistory>> getEntityHistoryList(final String searchToken) {

		return new Callable<List<EntityHistory>>() {
			@Override
			public List<EntityHistory> call() throws Exception {
				return privateEntityRepositoryImpl.getEntityHistoryList(searchToken);
			}
		};

	}

	private Callable<List<EntityBacktest>> getEntityBacktestList(final String searchToken) {

		return new Callable<List<EntityBacktest>>() {
			@Override
			public List<EntityBacktest> call() throws Exception {
				return privateEntityRepositoryImpl.getEntityBacktestList(searchToken);
			}
		};

	}

	public UpdateDefinition updateState(String searchToken, String state) throws Exception {

		UpdateDefinition ud = new UpdateDefinition();

		Future<List<EntityHistory>> entityHistoryLstF = taskExecutor.submit(getEntityHistoryList(searchToken));
		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));
		Future<List<EntityBacktest>> entityBacktestLstF = taskExecutor.submit(getEntityBacktestList(searchToken));

		List<PrivateEntity> privateEntities = privateEntityLstF.get();
		List<EntityHistory> entityHistoryLst = entityHistoryLstF.get();
		List<EntityBacktest> entityBacktestLst = entityBacktestLstF.get();

		checkList(privateEntities, entityBacktestLst);


		PrivateEntity pe = privateEntities.get(0);
		ud.setUpdateTime(pe.getUpdateTime());
		String status = pe.getStatus();
		if (STATE_ACTIVE.equalsIgnoreCase(state) && !STATE_DEV.equalsIgnoreCase(status) && !STATE_LIVE.equalsIgnoreCase(status)) {
			if (StringUtils.isNotEmpty(pe.getDefinition_live())) {
				pe = privateEntityRepositoryImpl.updateState(searchToken, STATE_LIVE, pe.getId());
			} else {
				pe = privateEntityRepositoryImpl.updateState(searchToken, STATE_DEV, pe.getId());
			}
		} else if (STATE_INACTIVE.equalsIgnoreCase(state) && !STATE_INACTIVE.equalsIgnoreCase(status)) {
			pe = privateEntityRepositoryImpl.updateState(searchToken, STATE_INACTIVE, pe.getId());
		}

		ud.setEntityHistory(entityHistoryLst);
		privateEntities = new ArrayList<PrivateEntity>();
		privateEntities.add(pe);
		ud.setPrivateEntities(privateEntities);

		return ud;
	}

	public List<PrivateEntityList> getPrivateEntityListByTaxonomyDirective(List<String> taxonomyDirectiveLst) throws Exception {
		return privateEntityRepositoryImpl.getPrivateEntityListByTaxonomyDirective(taxonomyDirectiveLst);
	}

	public void createPrivateEntity(String searchToken, CreateInputBean inputbean, String definition, List<PrivateEntity> privateEntities)
			throws Exception {

		// get Definition
		List<PrivateEntity> privateEntities1 = privateEntityRepositoryImpl.getDefinitions(searchToken);

		if (CollectionUtils.isNotEmpty(privateEntities1)) {
			privateEntities.addAll(privateEntities1);
			throw new CustomExceptionSuccess("Entity already exists : " + searchToken, 119); // entity already exists;
		}

		// persist into db
		privateEntityRepositoryImpl.insertIntoPrivateEntityTable(searchToken, inputbean,
				EnterpriseConfigThreadLocal.get().getId().toString(), definition);

		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));

		List<PrivateEntity> privateEntities11 = getAndValidateList(privateEntityLstF);

		privateEntities.addAll(privateEntities11);
	}

	private List<PrivateEntity> getAndValidateList(final Future<List<PrivateEntity>> privateEntityLstF) throws ExecutionException, InterruptedException {
		List<PrivateEntity> privateEntities11 = privateEntityLstF.get();
		
		if (CollectionUtils.isEmpty(privateEntities11)) {
			throw new CustomExceptionError(118); // entity already exists;
		}
		return privateEntities11;
	}

	public UpdateDefinition promote(String searchToken, CreateInputBean inputBean) throws Exception {

		UpdateDefinition updateDefinition = new UpdateDefinition();
		// get Definition
		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));
		Future<List<EntityBacktest>> entityBacktestLstF = taskExecutor.submit(getEntityBacktestList(searchToken));
		Future<List<EntityHistory>> entityHistoryLstF = taskExecutor.submit(getEntityHistoryList(searchToken));

		List<PrivateEntity> privateEntities = privateEntityLstF.get();
		List<EntityBacktest> entityBacktestLst = entityBacktestLstF.get();
		List<EntityHistory> entityHistoryLst = entityHistoryLstF.get();

		checkList(privateEntities, entityBacktestLst);

		checkInActiveEntity(searchToken, privateEntities);

		String name = inputBean.getRequester() != null ? inputBean.getRequester().getName() : inputBean.getAuthor().getName();
		String email = inputBean.getRequester() != null ? inputBean.getRequester().getEmail() : inputBean.getAuthor().getEmail();
		privateEntityRepositoryImpl.promote(searchToken, privateEntities.get(0), name, email);

		updateDefinition.setPrivateEntities(privateEntities);
		updateDefinition.setEntityHistory(entityHistoryLst);

		return updateDefinition;
	}

	private void checkList(final List<PrivateEntity> privateEntities, final List<EntityBacktest> entityBacktestLst) {
		checkEntityListAndOperationList(privateEntities, entityBacktestLst);
	}

	public void checkInActiveEntity(String searchToken, List<PrivateEntity> privateEntities) {
		PrivateEntity pe = privateEntities.get(0);
		checkStatusInactive(pe); 
	}
 
	private void checkStatusInactive(final PrivateEntity pe) { 
		if (STATE_INACTIVE.equalsIgnoreCase(pe.getStatus())) { 
			throw new CustomExceptionError(419); 
		} 
	} 

	public void checkUpdateDefinitionChanged(String definition, List<PrivateEntity> privateEntities) {

		PrivateEntity pe = privateEntities.get(0);
		if (definition.equals(pe.getDefinition_dev())) {
			throw new CustomExceptionSuccess(426);
		}

	}

	public UpdateDefinition getDefinition(String searchToken) throws Exception {

		UpdateDefinition ud = new UpdateDefinition();
		// get Definition
		Future<List<EntityHistory>> entityHistoryLstF = taskExecutor.submit(getEntityHistoryList(searchToken));
		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));

		List<PrivateEntity> privateEntities = privateEntityLstF.get();
		List<EntityHistory> entityHistoryLst = entityHistoryLstF.get();

		if (CollectionUtils.isEmpty(privateEntities)) {
			throw new CustomExceptionError(118); // entity already exists;
		}

		ud.setPrivateEntities(privateEntities);
		ud.setEntityHistory(entityHistoryLst);
		return ud;
	}

	public String generateSearchToken(CreateInputBean inputbean) throws Exception {
		String type = (inputbean.getType().toLowerCase().equalsIgnoreCase("topics")
				|| inputbean.getType().toLowerCase().equalsIgnoreCase("topic")) ? "T" : "C";
		String searchToken = type + "-PW-" + inputbean.getBrandInitial() + ":"
				+ searchTokenUtil.generateSearchToken(inputbean.getName()).getSearchToken();
		return searchToken;
	}

	public UpdateDefinition updateDefinition(String searchToken, String definition, String user, String email) throws Exception {
		// get Definition

		UpdateDefinition ud = new UpdateDefinition();

		Future<List<EntityHistory>> entityHistoryLstF = taskExecutor.submit(getEntityHistoryList(searchToken));
		Future<List<PrivateEntity>> privateEntityLstF = taskExecutor.submit(getPrivateEntityList(searchToken));
		Future<List<EntityBacktest>> entityBacktestLstF = taskExecutor.submit(getEntityBacktestList(searchToken));

		List<PrivateEntity> privateEntities = privateEntityLstF.get();
		List<EntityHistory> entityHistoryLst = entityHistoryLstF.get();
		List<EntityBacktest> entityBacktestLst = entityBacktestLstF.get();

		checkEntityListAndOperationList(privateEntities, entityBacktestLst);

		checkInActiveEntity(searchToken, privateEntities);
		checkUpdateDefinitionChanged(definition, privateEntities);

		privateEntityRepositoryImpl.updateDefinition(searchToken, definition, privateEntities.get(0), user, email);

		ud.setEntityHistory(entityHistoryLst);
		ud.setPrivateEntities(privateEntities);

		return ud;
	}

	private void checkEntityListAndOperationList(final List<PrivateEntity> privateEntities, final List<EntityBacktest> entityBacktestLst) {
		if (CollectionUtils.isEmpty(privateEntities)) {
			throw new CustomExceptionError(118); // entity not found;
		}
		
		if (CollectionUtils.isNotEmpty(entityBacktestLst)) {
			throw new CustomExceptionError(423); // Operation not allowed, as backtest is already in progress for this entity.
		}
	}
	
	public JsonNode getJsonNodeRes(String res) throws JsonProcessingException, IOException {
		
		if (StringUtils.isEmpty(res)) {
			return null;
		}
		
		return jsonMapper.readTree(res);
	}
	

}
