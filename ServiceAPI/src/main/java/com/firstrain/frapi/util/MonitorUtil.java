package com.firstrain.frapi.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Tags.SearchOrderType;
import com.firstrain.frapi.obj.MonitorObj;
import com.firstrain.frapi.obj.MonitorObj.Type;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.SearchTokenEntry;

@Service
public class MonitorUtil {

	private static final Logger LOG = Logger.getLogger(MonitorUtil.class);

	@Autowired
	private com.firstrain.frapi.service.EntityProcessingService entityProcessingService;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private com.firstrain.frapi.repository.EntityBaseServiceRepository entityBaseServiceRepository;

	public List<Long> getOrderedSearches(List<Items> folderItems, SearchOrderType searchOrderType, Tags tag) throws Exception {

		if (searchOrderType == SearchOrderType.NAME) {
			Collections.sort(folderItems, new Comparator<Items>() {
				@Override
				public int compare(Items o1, Items o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
		} else if (searchOrderType == SearchOrderType.DATE) {
			Collections.sort(folderItems, new Comparator<Items>() {
				@Override
				public int compare(Items o1, Items o2) {
					// for newly created items, date will be null
					Timestamp t1 = ((o1.getAuditData() != null && o1.getInsertTime() != null) ? o1.getInsertTime()
							: new Timestamp(System.currentTimeMillis()));
					Timestamp t2 = ((o2.getAuditData() != null && o2.getInsertTime() != null) ? o2.getInsertTime()
							: new Timestamp(System.currentTimeMillis()));
					return -t1.compareTo(t2);
				}
			});
		}
		List<Long> orderedSearches = new ArrayList<Long>(folderItems.size());
		for (Items items : folderItems) {
			orderedSearches.add(items.getId());
		}
		return orderedSearches;
	}

	public void setEntityType(MonitorObj f, SearchTokenEntry token) {
		int type = token.type;
		if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
			f.setType(Type.TYPE_COMPANY);
			List<com.firstrain.frapi.obj.MonitorObj> bls = getBusinessLine(token.searchToken);
			f.setBizLines((bls != null && !bls.isEmpty()));

		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
			f.setType(Type.TYPE_TOPIC);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
			f.setType(Type.TYPE_INDUSTRY);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_PEOPLE) {
			f.setType(Type.TYPE_PERSON);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL) {
			f.setType(Type.TYPE_TOPIC);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_SOURCE) {
			f.setType(Type.TYPE_SOURCE);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_REGION) {
			f.setType(Type.TYPE_REGION);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_CONTENT_TYPE) {
			f.setType(Type.TYPE_CONTENT_TYPE);
		} else if (type == SearchTokenEntry.SEARCH_TOKEN_TAG_BIZLINE) {
			f.setType(Type.TYPE_BUSINESS_LINE);
		} else {
			f.setType(Type.TYPE_DEFAULT);
		}
	}

	private List<MonitorObj> getBusinessLine(String token) {
		IEntityInfo entityInfo = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(token);
		if (entityInfo == null) {
			LOG.debug("Dropped token " + token + " as not available in global entity cache.");
			return null;
		}
		Collection<Integer> bizLineCatIds = entityInfo.getBizLineCatIds();
		List<MonitorObj> businessLines = new ArrayList<MonitorObj>();
		if (bizLineCatIds != null) {
			for (int bizLineId : bizLineCatIds) {
				try {
					IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(Integer.toString(bizLineId));

					if (ei == null) {
						LOG.warn("Could not get entity for catid " + bizLineId);
						continue;
					}
					MonitorObj filter = new MonitorObj(ei.getName(), ei.getSearchToken());
					filter.setExclude(true);// excluded by default
					businessLines.add(filter);
				} catch (Exception e) {
					LOG.debug("Exception parsing Business Line catid " + bizLineId);
				}
			}
		}
		return businessLines;
	}
}
