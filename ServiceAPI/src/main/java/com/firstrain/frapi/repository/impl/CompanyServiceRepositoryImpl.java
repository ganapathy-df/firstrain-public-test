package com.firstrain.frapi.repository.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.ManagementChange;
import com.firstrain.frapi.domain.ManagementChange.MgmtChangeType;
import com.firstrain.frapi.domain.MgmtSummary;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TimeUtils;

@Repository
@Qualifier("companyServiceRepositoryImpl")
public class CompanyServiceRepositoryImpl extends EntityBaseServiceRepositoryImpl implements CompanyServiceRepository {

	@Autowired
	private ConvertUtil convertUtil;
	private final Logger LOG = Logger.getLogger(CompanyServiceRepositoryImpl.class);
	private static final Map<CompanyTeam, Integer> TEAM_CONS_MAP = new HashMap<CompanyTeam, Integer>();
	private final short BASE_YEAR = 1970;
	private final String[] MGMT_DETAIL_FIELDS = {"trendId", "normalizedName", "eventDate", "mgmtBucketTypeForUI", "newCompanyId", "newGroup",
			"newRegion", "newTitleStandardized", "oldCompanyId", "oldGroup", "oldRegion", "oldTitleStandardized", "reportDate", "supportingUrl",
			"newCompanyName", "oldCompanyName"};

	static {
		TEAM_CONS_MAP.put(CompanyTeam.BOARD_OF_DIRECTORS, 1);
		TEAM_CONS_MAP.put(CompanyTeam.OFFICERS, 2);
		TEAM_CONS_MAP.put(CompanyTeam.EXECUTIVE, 4);
		TEAM_CONS_MAP.put(CompanyTeam.MANAGEMENT, 8);
	}

	@Override
	public SolrDocument getCompanyInfoFromIndex(String searchToken) throws SolrServerException {
		String query = "type:Company AND attrSearchToken:\"" + searchToken + "\"";
		SolrDocumentList results = SolrServerReader.retrieveSolrDocsInBatches(getEntitySolrServer(), query, 1);
		if (results.getNumFound() == 0) {
			LOG.debug("Company not found for token " + searchToken);
			return null;
		}

		if (results.getNumFound() > 1) {
			LOG.warn("Multiple Companies found for token " + searchToken + " . Ambigous results possible.");
		}
		SolrDocument compDoc = results.get(0);
		return compDoc;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void getMgmtTurnoverMonthlySummary(MgmtTurnoverData mgmtTurnover, int companyId, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException {

		NamedList<Object> results = getMgmtTurnoverMonthlySummary(companyId, spec);
		Calendar cal = retrieveAndUpdateMonthDate(spec); 
		
		int startMonth = cal.get(Calendar.MONTH);
		int startYear = cal.get(Calendar.YEAR);
		if (results != null) {
			for (Map.Entry<String, Object> e : results) {
				NamedList<Object> entity = (NamedList<Object>) e.getValue();
				setTurnoverInfo(mgmtTurnover, entity, startMonth, startYear, true, spec);
			}
		}
	}

	@Override
	public List<ManagementChange> getMgmtTurnoverDetails(List<Integer> companyIDs, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException {

		List<ManagementChange> detailList = null;
		if (!spec.details) {
			return detailList;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(spec.lastDay);
		cal.add(Calendar.DATE, -spec.days);
		DateRange dateRange = new DateRange(cal.getTime(), spec.lastDay, "");

		String query = "-attrExclude:all AND type:MgmtTrend AND " + getChangeTypeCondn(spec, companyIDs) + " AND reportDateMinuteId:["
				+ TimeUtils.getMinuteNumber(dateRange.getStart(), BASE_YEAR) + " TO " + TimeUtils.getMinuteNumber(dateRange.getEnd(), BASE_YEAR)
				+ "]";

		if (LOG.isDebugEnabled()) {
			LOG.debug("Query getDetails: " + query);
		}

		SolrDocumentList docList = SolrServerReader.retrieveNSolrDocs(getPeopleServer(), query, spec.getStart(), spec.getRows(),
				"reportDateMinuteId", false, MGMT_DETAIL_FIELDS);

		if (docList != null && !docList.isEmpty()) {
			detailList = new ArrayList<ManagementChange>(docList.size());
			for (SolrDocument d : docList) {
				detailList.add(getMgmtDetails(companyIDs, d, spec));
			}
			spec.setTotal(docList.getNumFound());
		}
		return detailList;
	}

	@Override
	public List<Entity> getMatchedCompanyForQ(String q) throws Exception {
		if (q.isEmpty()) {
			return null;
		}
		List<Entity> entityList = new ArrayList<Entity>();
		SolrDocumentList solrDocumentList =
				SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), q, 0, 10, "attrCompanyId,dnbCompanyId");
		IEntityInfoCache entityInfoCache = this.getEntityInfoCache();
		if (solrDocumentList != null && !solrDocumentList.isEmpty()) {
			for (SolrDocument doc : solrDocumentList) {
				String companyIdStr = (String) doc.getFieldValue("attrCompanyId");
				String dnbCompanyId = (String) doc.getFieldValue("dnbCompanyId");
				IEntityInfo entityInfo = entityInfoCache.companyIdToEntity(Integer.parseInt(companyIdStr));
				Entity entity = convertUtil.convertEntityInfo(entityInfo);
				entity.setDnbEntityId(dnbCompanyId);
				entityList.add(entity);
			}
		} else {
			LOG.debug("No Company found after solr search for q = " + q);
		}
		return entityList;
	}

	@Override
	public SolrDocumentList getCompanySolrDocsForQuery(String q) throws Exception {
		if (q.isEmpty()) {
			return null;
		}
		String[] fields =
				{"attrCompanyId", "attrWebsite", "attrZip", "attrCity", "attrCountry", "attrAddress", "attrStateName", "attrStateNameAbr"};
		return SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), q, 0, 50, fields);
	}

	@Override
	public SolrDocumentList getCompanyDetailsFromCompanyIds(String compIds, int count) throws Exception {
		if (compIds.isEmpty()) {
			return null;
		}
		String[] fields =
				{"attrCompanyId", "attrWebsite", "attrZip", "attrCity", "attrCountry", "attrAddress", "attrStateName", "attrStateNameAbr"};
		String query = "attrCompanyId:(" + compIds + ")";
		SolrDocumentList solrDocumentList = SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, 0, count, fields);
		return solrDocumentList;
	}

	@SuppressWarnings("unchecked")
	private NamedList<Object> getMgmtTurnoverMonthlySummary(int companyId, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException {
		if (!spec.monthlyCount) {
			return null;
		}
		SimpleDateFormat simpleFormat = new SimpleDateFormat("MM-dd-yyyy");
		SolrQuery query = new SolrQuery();
		query.setQueryType("/admin/MgmtTrendSummary.jsp");
		query.setParam("companyIDs", String.valueOf(companyId));
		query.setParam("mgmtBucketType", getMgmtBucketTypes(spec.changeLevels, ","));
		query.set("debug", LOG.isDebugEnabled());
		query.setParam("tDt", simpleFormat.format(new Date()));

		Calendar cal = retrieveAndUpdateMonthDate(spec); 
		
		query.setParam("fDt", simpleFormat.format(cal.getTime()));
		QueryResponse solrRes = SolrSearcher.runQueryRequest(getPeopleServer(), query, true);
		return (NamedList<Object>) solrRes.getResponse().get("result");
	}
 
	private Calendar retrieveAndUpdateMonthDate(final MgmtTurnoverServiceSpec spec) { 
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, -(spec.getMonthlyCountForMonth() - 1)); 
		cal.add(Calendar.DATE, -cal.get(Calendar.DATE) + 1); 
		return cal; 
	} 
	

	private String getMgmtBucketTypes(CompanyTeam changeLevels[], String del) {
		StringBuilder sb = new StringBuilder();
		for (CompanyTeam cl : changeLevels) {
			sb.append(TEAM_CONS_MAP.get(cl)).append(del);
		}
		return sb.substring(0, sb.length() - del.length());
	}

	@SuppressWarnings("unchecked")
	private void setTurnoverInfo(MgmtTurnoverData mgmtTurnoverData, NamedList<Object> entity, int startMonthParam, int startYear, boolean monthly,
			MgmtTurnoverServiceSpec spec) {
		int startMonth = startMonthParam;
		List<Integer> internalMoves = (List<Integer>) entity.get("internalMoves");
		List<Integer> departures = (List<Integer>) entity.get("departures");
		List<Integer> hires = (List<Integer>) entity.get("hires");
		int startQuarter = (startMonth / 3) + 1;
		int total = 0;
		int max = 0;
		List<MgmtSummary> infoList = new ArrayList<MgmtSummary>(internalMoves.size());
		for (int i = 0; i < internalMoves.size(); i++) {
			MgmtSummary info = new MgmtSummary();
			if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.INTERNALMOVE)) {
				int turnover = internalMoves.get(i);
				info.setOthersInternalMoveCount(turnover);
				total += turnover;
				max = Math.max(turnover, max);
			}
			if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.DEPARTURE)) {
				int turnover = departures.get(i);
				info.setOthersDepartureCount(turnover);
				total += turnover;
				max = Math.max(turnover, max);
			}
			if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.HIRE)) {
				int turnover = hires.get(i);
				info.setOthersHireCount(hires.get(i));
				total += turnover;
				max = Math.max(turnover, max);
			}
			info.setReportYear(startYear);
			if (monthly) {
				info.setReportMonth(startMonth + 1);// setting month 1-12 range
				startMonth++;
				if (startMonth > 11) {
					startMonth = 0;
					startYear++;
				}
			} else {// quarterly
				info.setReportQuarter(startQuarter);
				startQuarter++;
				if (startQuarter > 4) {
					startQuarter = 1;
					startYear++;
				}
			}
			infoList.add(info);
		}
		// TODO what to do for quarterly summary.
		populateMonthlyData(infoList, internalMoves, mgmtTurnoverData, monthly, total);
	}

	private void populateMonthlyData(final List<MgmtSummary> infoList, final List<Integer> internalMoves, final MgmtTurnoverData mgmtTurnoverData, final boolean monthly, final int total) {
		if (monthly) {
			mgmtTurnoverData.setMonthlySummary(infoList);
			mgmtTurnoverData.setTotalTurnoverMth(total);
			mgmtTurnoverData.setAverageTurnoverMth(total * 1.0F / internalMoves.size());
		}
	}


	private String getChangeTypeCondn(MgmtTurnoverServiceSpec spec, List<Integer> companyIDs) {
		String companyIDStr = FR_ArrayUtils.getStringFromCollection(companyIDs, " ");
		String changeLevelStr = getMgmtBucketTypes(spec.changeLevels, " ");
		String query = null;
		if (spec.changeTypes.length == MgmtChangeType.values().length) {
			query = "(( newCompanyId:(" + companyIDStr + ") AND mgmtBucketType:(" + changeLevelStr + "))" + " OR ( oldCompanyId:(" + companyIDStr
					+ ") AND mgmtBucketTypeOld:(" + changeLevelStr + ")))";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.INTERNALMOVE)
				&& FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.HIRE)) {
			query = " newCompanyId:(" + companyIDStr + ")" + " AND ( mgmtBucketType:(" + changeLevelStr + ") OR (mgmtBucketTypeOld:("
					+ changeLevelStr + ") AND isInternalMove:1))";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.INTERNALMOVE)
				&& FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.DEPARTURE)) {
			query = " oldCompanyId:(" + companyIDStr + ")" + " AND ( (mgmtBucketType:(" + changeLevelStr
					+ ") AND isInternalMove:1) OR mgmtBucketTypeOld:(" + changeLevelStr + "))";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.HIRE)
				&& FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.DEPARTURE)) {
			query = " isInternalMove:0 AND ( ( newCompanyId:(" + companyIDStr + ") AND mgmtBucketType:(" + changeLevelStr + "))"
					+ " OR ( oldCompanyId:(" + companyIDStr + ") AND mgmtBucketTypeOld:(" + changeLevelStr + ")))";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.INTERNALMOVE)) {
			query = " isInternalMove:1 AND newCompanyId:(" + companyIDStr + ")" + " AND ( mgmtBucketType:(" + changeLevelStr
					+ ") OR mgmtBucketTypeOld:(" + changeLevelStr + "))";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.DEPARTURE)) {
			query = " isInternalMove:0 AND oldCompanyId:(" + companyIDStr + ") AND mgmtBucketTypeOld:(" + changeLevelStr + ")";
		} else if (FR_ArrayUtils.searchArray(spec.changeTypes, com.firstrain.utils.MgmtChangeType.HIRE)) {
			query = " isInternalMove:0 AND newCompanyId:(" + companyIDStr + ") AND mgmtBucketType:(" + changeLevelStr + ")";
		}
		return query;
	}

	private ManagementChange getMgmtDetails(List<Integer> companyIDs, SolrDocument d, MgmtTurnoverServiceSpec spec) {

		ManagementChange changeDetail = new ManagementChange();
		long trendId = -1;
		if (d.getFieldValue("trendId") != null) {
			trendId = (Long) d.getFieldValue("trendId");
			changeDetail.setTrendId(trendId);
		}
		int newCompanyID = -1;
		Integer compID = (Integer) d.getFieldValue("newCompanyId");
		if (compID != null) {
			newCompanyID = compID;
			IEntityInfo entity = this.getEntityInfoCache().companyIdToEntity(newCompanyID);
			if (entity != null) {
				changeDetail.setNewCompanyId(newCompanyID);
				changeDetail.setNewCompany(entity.getName());
				changeDetail.setNewTicker(entity.getPrimaryTicker());
			}
		}

		int oldCompanyID = -1;
		compID = (Integer) d.getFieldValue("oldCompanyId");
		if (compID != null) {
			oldCompanyID = compID;
			IEntityInfo entity = this.getEntityInfoCache().companyIdToEntity(oldCompanyID);
			if (entity != null) {
				changeDetail.setOldCompanyId(oldCompanyID);
				changeDetail.setOldCompany(entity.getName());
				changeDetail.setOldTicker(entity.getPrimaryTicker());
			}
		}
		if (changeDetail.getNewCompany() == null) {
			changeDetail.setNewCompany((String) d.getFieldValue("newCompanyName"));
		}
		if (changeDetail.getOldCompany() == null) {
			changeDetail.setOldCompany((String) d.getFieldValue("oldCompanyName"));
		}

		if (newCompanyID == oldCompanyID) { // Internal move
			changeDetail.setChangeType(MgmtChangeType.INTERNALMOVE.name());
		} else if (companyIDs.contains(oldCompanyID) && FR_ArrayUtils.searchArray(spec.changeTypes, MgmtChangeType.DEPARTURE)) { // Departure
			changeDetail.setChangeType(MgmtChangeType.DEPARTURE.name());
		} else if (companyIDs.contains(newCompanyID) && FR_ArrayUtils.searchArray(spec.changeTypes, MgmtChangeType.HIRE)) { // Hire
			changeDetail.setChangeType(MgmtChangeType.HIRE.name());
		}

		changeDetail.setDate((Date) d.getFieldValue("eventDate"));
		changeDetail.setFutureEvent(changeDetail.getDate().after(new Date()));
		changeDetail.setPerson((String) d.getFieldValue("normalizedName"));
		changeDetail.setUrl((String) d.getFieldValue("supportingUrl"));

		changeDetail.setNewRegion((String) d.getFieldValue("newRegion"));
		changeDetail.setNewGroup((String) d.getFieldValue("newGroup"));
		changeDetail.setNewDesignation((String) d.getFieldValue("newTitleStandardized"));

		changeDetail.setOldRegion((String) d.getFieldValue("oldRegion"));
		changeDetail.setOldGroup((String) d.getFieldValue("oldGroup"));
		changeDetail.setOldDesignation((String) d.getFieldValue("oldTitleStandardized"));
		return changeDetail;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public List<String> getCompetitorCatIdsFromSolr(int companyId) throws Exception {

		Collection<String> comptCatIdsList = null;
		String query = "attrCompanyId:" + companyId;
		SolrDocumentList solrDocumentList = SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, 0, 1, "attrCompetitorCatId");
		if (solrDocumentList != null && !solrDocumentList.isEmpty()) {
			comptCatIdsList = (Collection) solrDocumentList.get(0).getFieldValues("attrCompetitorCatId");
			return (List<String>) comptCatIdsList;
		}
		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Map<String, List<String>> getBLVsCompetitorsCatIdsFromSolr(int companyId, List<String> searchTokenList) throws Exception {

		String query = "attrCompanyId:" + companyId;
		// String[] FIELDS_TO_RETURN = new String[] {"bLCategoryIdList",
		// "competitorCategoryIdListPerBl"};

		String[] FIELDS_TO_RETURN = new String[] {"bizLineCatIds", "competitorListPerBl"};

		SolrDocumentList solrDocumentList = SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, 0, 1, FIELDS_TO_RETURN);

		List<Integer> bLCategoryIdList = null;
		List<Object> competitorCategoryIdListPerBl = null;

		if (solrDocumentList != null && !solrDocumentList.isEmpty()) {

			bLCategoryIdList = (List<Integer>) ((Collection) solrDocumentList.get(0).getFieldValues("bizLineCatIds"));
			competitorCategoryIdListPerBl = (List<Object>) solrDocumentList.get(0).getFieldValues("competitorListPerBl");

			LOG.debug("bLCategoryIdList: " + bLCategoryIdList);
			LOG.debug("competitorCategoryIdListPerBl: " + competitorCategoryIdListPerBl);
		}

		Map<String, List<String>> blVsCompetitorsMap = new HashMap<String, List<String>>();

		if (bLCategoryIdList != null && competitorCategoryIdListPerBl != null) {

			for (String searchToken : searchTokenList) {
				IEntityInfo entityInfo = getEntityInfoCache().searchTokenToEntity(searchToken);
				if (entityInfo != null) {
					if (!bLCategoryIdList.contains(Integer.valueOf(entityInfo.getId()))) {
						LOG.debug("bL: " + searchToken + " with id:" + entityInfo.getId() + " is not a valid BL for this companyId:" + companyId);
						continue;
					}

					int index = bLCategoryIdList.indexOf(Integer.valueOf(entityInfo.getId()));
					String competitorsCSV = (String) competitorCategoryIdListPerBl.get(index);

					List<String> competitorList = FR_ArrayUtils.csvToArrayList(competitorsCSV);
					LOG.debug("competitor list:" + competitorList);
					blVsCompetitorsMap.put(entityInfo.getId(), competitorList);
				}
			}
		}

		return blVsCompetitorsMap;
	}
}
