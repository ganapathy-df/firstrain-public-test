package com.firstrain.frapi.repository;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.ManagementChange;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.solr.client.SearchException;

@Repository
public interface CompanyServiceRepository extends EntityBaseServiceRepository {

	public void getMgmtTurnoverMonthlySummary(MgmtTurnoverData mgmtTurnover, int companyId, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException;

	public List<ManagementChange> getMgmtTurnoverDetails(List<Integer> companyIDs, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException;

	public List<Entity> getMatchedCompanyForQ(String q) throws Exception;

	public SolrDocumentList getCompanyDetailsFromCompanyIds(String compIds, int count) throws Exception;

	public SolrDocument getCompanyInfoFromIndex(String searchToken) throws SolrServerException;

	public SolrDocumentList getCompanySolrDocsForQuery(String q) throws Exception;

	public List<String> getCompetitorCatIdsFromSolr(int companyId) throws Exception;

	public Map<String, List<String>> getBLVsCompetitorsCatIdsFromSolr(int companyId, List<String> searchTokenList) throws Exception;

}
