package com.firstrain.frapi.obj;

import java.util.List;

import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;


/**
 * Specification for generating graph data, most of the parameters are optional.
 * <p>
 * Parameters should be set according to requirements, for ex: to generate trading map set the appropriate companyId, to generate web volume
 * data from MIDB set categoryId properly, to generate web volume from SOLR document index set searchTokens properly...
 * <p>
 * Default no of days is specified by {@link GraphDataProvider}.GRAPH_OPERATIONAL_DAYS(180), default scope is BROAD(SearchSpec.SCOPE_BROAD).
 * </p>
 * </p>
 * 
 * @author Akanksha
 */
public class GraphQueryCriteria {

	/**
	 * Max days to consider for graph data generation(180).
	 */
	public static final int GRAPH_OPERATIONAL_DAYS = 180;

	private int companyId = -1;
	private int categoryId = -1;
	private int numberOfDays = GRAPH_OPERATIONAL_DAYS;
	private int scope = SearchSpec.SCOPE_BROAD;
	private DateRange dateRange;
	private List<SearchTokenEntry> searchTokens;

	public GraphQueryCriteria() {
		super();
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public void setNumberOfDays(int numberOfDays) {
		if (numberOfDays > GRAPH_OPERATIONAL_DAYS) {
			throw new IllegalArgumentException("numberOfDays can't be greater than " + GRAPH_OPERATIONAL_DAYS);
		}
		this.numberOfDays = numberOfDays;
	}

	public int getNumberOfDays() {
		return this.numberOfDays;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getScope() {
		return this.scope;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

	public DateRange getDateRange() {
		return this.dateRange;
	}

	public void setSearchTokens(List<SearchTokenEntry> searchTokens) {
		this.searchTokens = searchTokens;
	}

	public List<SearchTokenEntry> getSearchTokens() {
		return this.searchTokens;
	}

	@Override
	public String toString() {
		return String.format(
				"GraphQueryCriteria [getCategoryId()=%s, getCompanyId()=%s, getDateRange()=%s, getNumberOfDays()=%s, getScope()=%s, getSearchTokens()=%s]",
				this.getCategoryId(), this.getCompanyId(), this.getDateRange(), this.getNumberOfDays(), this.getScope(),
				this.getSearchTokens());
	}
}

