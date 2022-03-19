package com.firstrain.frapi.pojo;

import java.util.List;

import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

public class EntityBriefInfo {

	private DocumentSet webResults;
	private TweetSet tweetList;
	private DocumentSet analystComments;
	private EventSet mgmtChangeEvents;
	private Graph webVolumeGraph;
	private EventSet eventsTimeline;
	private MgmtTurnoverData mgmtTurnoverData;
	private VisualizationData visualizationData;
	private List<Entity> matchedEntity;
	private Entity entity;
	private EntityMap entityMap;
	private int statusCode;
	private String scopeDirective;

	/**
	 * @return the webResults
	 */
	public DocumentSet getWebResults() {
		return webResults;
	}

	/**
	 * @param webResults the webResults to set
	 */
	public void setWebResults(DocumentSet webResults) {
		this.webResults = webResults;
	}

	/**
	 * @return the tweetList
	 */
	public TweetSet getTweetList() {
		return tweetList;
	}

	/**
	 * @param tweetList the tweetList to set
	 */
	public void setTweetList(TweetSet tweetList) {
		this.tweetList = tweetList;
	}

	/**
	 * @return the analystComments
	 */
	public DocumentSet getAnalystComments() {
		return analystComments;
	}

	/**
	 * @param analystComments the analystComments to set
	 */
	public void setAnalystComments(DocumentSet analystComments) {
		this.analystComments = analystComments;
	}

	/**
	 * @return the mgmtChangeEvents
	 */
	public EventSet getMgmtChangeEvents() {
		return mgmtChangeEvents;
	}

	/**
	 * @param mgmtChangeEvents the mgmtChangeEvents to set
	 */
	public void setMgmtChangeEvents(EventSet mgmtChangeEvents) {
		this.mgmtChangeEvents = mgmtChangeEvents;
	}

	/**
	 * @return the webVolumeGraph
	 */
	public Graph getWebVolumeGraph() {
		return webVolumeGraph;
	}

	/**
	 * @param webVolumeGraph the webVolumeGraph to set
	 */
	public void setWebVolumeGraph(Graph webVolumeGraph) {
		this.webVolumeGraph = webVolumeGraph;
	}

	/**
	 * @return the eventsTimeline
	 */
	public EventSet getEventsTimeline() {
		return eventsTimeline;
	}

	/**
	 * @param eventsTimeline the eventsTimeline to set
	 */
	public void setEventsTimeline(EventSet eventsTimeline) {
		this.eventsTimeline = eventsTimeline;
	}

	/**
	 * @return the mgmtTurnoverData
	 */
	public MgmtTurnoverData getMgmtTurnoverData() {
		return mgmtTurnoverData;
	}

	/**
	 * @param mgmtTurnoverData the mgmtTurnoverData to set
	 */
	public void setMgmtTurnoverData(MgmtTurnoverData mgmtTurnoverData) {
		this.mgmtTurnoverData = mgmtTurnoverData;
	}

	public VisualizationData getVisualizationData() {
		return visualizationData;
	}

	public void setVisualizationData(VisualizationData visualizationData) {
		this.visualizationData = visualizationData;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<Entity> getMatchedEntity() {
		return matchedEntity;
	}

	public void setMatchedEntity(List<Entity> matchedEntity) {
		this.matchedEntity = matchedEntity;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void updatePrep(BaseSet baseSet) {

		switch (baseSet.getSectionType()) {

		case HR:
			this.setWebResults((DocumentSet) baseSet);
			break;	
		case FR:
			this.setWebResults((DocumentSet) baseSet);
			break;
		case FT:
			this.setTweetList((TweetSet) baseSet);
			break;
		case AC:
			this.setAnalystComments((DocumentSet) baseSet);
			break;
		case TE:
			this.setMgmtChangeEvents((EventSet) baseSet);
			break;
		case WV:
			this.setWebVolumeGraph((Graph) baseSet);
			break;
		case E:
			this.setEventsTimeline((EventSet) baseSet);
			break;
		case MTC:
			this.setMgmtTurnoverData((MgmtTurnoverData) baseSet);
			break;
		case VIZ:
			this.setVisualizationData((VisualizationData) baseSet);
			break;
		default:
			break;
		}
	}

	public void setPerfStats(BaseSet obj) {
		PerfRequestEntry entry = obj.getStat();
		PerfMonitor.mergeStats(entry, PerfActivityType.Others, true);
	}

	public EntityMap getEntityMap() {
		return this.entityMap;
	}

	public void setEntityMap(EntityMap entityMap) {
		this.entityMap = entityMap;
	}

	public String getScopeDirective() {
		return scopeDirective;
	}

	public void setScopeDirective(String scopeDirective) {
		this.scopeDirective = scopeDirective;
	}

}
