package com.firstrain.frapi.util;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Document.DocQuote;
import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.Monitor;
import com.firstrain.frapi.domain.SecEventMeta;
import com.firstrain.frapi.domain.StockPriceMeta;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;
import com.firstrain.frapi.util.DefaultEnums.TitleType;
import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.mip.object.FR_IEventEntity;
import com.firstrain.mip.object.FR_IStartURL;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_DateUtils;
import com.firstrain.utils.MgmtChangeType;
import com.firstrain.utils.TitleUtils;

@Service
public class ConvertUtil {

	private final Logger LOG = Logger.getLogger(ConvertUtil.class);
	private final float SUMMARY_TRIM_FACTOR = 0.15f;
	private final int MIN_SUMMARY_LENGTH = 100;
	private final int MAX_SUMMARY_LENGTH = 400;

	public User convertDbUserToDomainUser(Users dbUser, MembershipType type) throws Exception {
		if (dbUser == null) {
			return null;
		}
		User user = getDomainUserFromDBUser(dbUser);
		user.setMembershipType(convertToBaseMembershipType(type));
		return user;
	}

	public User getDomainUserFromDBUser(Users user) {
		User domainUser = null;
		if (user != null) {
			domainUser = new User();
			domainUser.setUserId("" + user.getId());
			domainUser.setDomain(user.getDomain());
			domainUser.setEmail(user.getEmail());
			domainUser.setFirstName(user.getFirstName());
			domainUser.setFlags(user.getFlags().name());
			domainUser.setFormat(user.getFormat());
			domainUser.setLastName(user.getLastName());
			domainUser.setMonitorOrderType(user.getMonitorOrderType().name());
			domainUser.setOwnedByType(user.getOwnedByType().name());
			domainUser.setOwnedBy(user.getOwnedBy());
			domainUser.setTemplateId(user.getTemplateID());
			domainUser.setTimeZone(user.getTimeZone());
			domainUser.setUserName(user.getUserName());
			domainUser.setUserCompany(user.getCompany());
			domainUser.setUserType(DefaultEnums.UserType.valueOf(user.getType().name()));
			domainUser.setOrigin(DefaultEnums.Origin.valueOf(user.getOrigin().name()));
		}
		return domainUser;
	}

	public DefaultEnums.MembershipType convertToBaseMembershipType(MembershipType type) {
		if (type == null) {
			return null;
		}
		return DefaultEnums.MembershipType.valueOf(type.name());
	}

	public Document convertDocumentPOJOFromDocEntry(DocEntry doc, ContentType contentType) {
		Document document = null;
		try {
			document = convertDocumentPOJOFromDocEntry(doc);
			if (contentType != null) {
				document.setContentType(contentType);
			}

		} catch (Exception ex) {
			LOG.error("Exception Converting DocEntryDocument!", ex);
		}
		return document;
	}



	public Monitor convertToTags(Tags tag) {
		if (tag == null) {
			return null;
		}
		Monitor monitor = new Monitor();
		monitor.setId(tag.getId());
		monitor.setName(tag.getTagName());
		monitor.setType(convertToBaseTagType(tag.getTagType()));
		monitor.setMailActive((tag.getEmailId() > 0));
		monitor.setOwnedByType(convertToBaseOwnedByType(tag.getOwnedByType()));
		return monitor;
	}

	public DefaultEnums.TagType convertToBaseTagType(Tags.TagType type) {
		if (type == null) {
			return null;
		}
		return DefaultEnums.TagType.valueOf(type.name());
	}

	public DefaultEnums.OwnedByType convertToBaseOwnedByType(OwnedByType type) {
		if (type == null) {
			return null;
		}
		return DefaultEnums.OwnedByType.valueOf(type.name());
	}

	public long getIdFromAbsoluteId(String absoluteId) {
		long id = -1;
		if (absoluteId != null) {
			String[] tokens = absoluteId.split(":");
			id = Long.parseLong(tokens[tokens.length - 1]);
		}
		return id;
	}

	public Document convertDocumentPOJOFromDocEntry(DocEntry doc) {

		Document document = new Document();
		try {
			document = convertDocumentPOJOFromDocEntryObject(doc);
			document.setGroupId(doc.getGroupId());
			if (doc.getSmartSummary() != null) {
				document.setSummary(doc.getSmartSummary());
			} else {
				document.setSummary(doc.getSummary());
			}
			if (document.getSummary() != null) {
				// trim summary - trim to MAX_SUMMARY_LENGTH or SUMMARY_TRIM_FACTOR of body length
				String trimmedSummary = getTrimmedSummary(document.getSummary(), doc);
				// show summary only if it's more than MIN_SUMMARY_LENGTH
				if (trimmedSummary.length() > MIN_SUMMARY_LENGTH) {
					document.setSummary(trimmedSummary);
				} else {
					document.setSummary(null);
				}
			}

			document.setScore(getCurrentScore(doc.docScore, document.getDate()));

			if (doc.getAccessType() == FR_IStartURL.ACCESS_TYPE_GENERAL_LOGIN
					|| doc.getAccessType() == FR_IStartURL.ACCESS_TYPE_PREMIUM_LOGIN) {
				document.setLoginRequired(true);
			}
			ContentType contentType = null;
			if (doc.catEntries != null) {
				outer: for (ContentType ct : ContentType.values()) {
					for (DocCatEntry docCat : doc.catEntries) {
						if (ct.getId() == Integer.parseInt(docCat.getEntity().getId())) {
							contentType = ct;
							break outer;
						}
					}
				}
			}
			if (contentType == null) {
				contentType = ContentType.WEBNEWS;
			}
			document.setContentType(contentType);
			document.setMatchedCompanies(getEntityPOJOList(doc.getMatchedCompanies(), contentType, false));
			document.setMatchedTopics(getEntityPOJOList(doc.getMatchedTopics(), contentType, false));
			document.setCatEntries(getEntityPOJOList(doc.getCatEntries(), contentType, false));

			if (doc.getFavIcon() != null) {
				document.setFavicon(doc.getFavIcon().getUrlLocal());
			}
			if (doc.getDocImage() != null) {
				document.setImage(doc.getDocImage().getUrlLocaliPad());
			}
			List<QuoteEntry> quotes = doc.getOtrQuotes();

			if (quotes != null && !quotes.isEmpty()) {
				document.setDocQuote(new DocQuote(quotes.get(0).getText(), quotes.get(0).getPerson()));
			}
			if (doc.getSimilarDocs() != null && !doc.getSimilarDocs().isEmpty()) {
				document.setDupCount(doc.getSimilarDocs().size());
			}

			/*
			 * if(doc.getNgrams() != null) { List<DocNgrams> ngrams = new ArrayList<DocNgrams>(doc.getNgrams().size()); for(Ngrams ngram :
			 * doc.getNgrams()) { DocNgrams docNgrams = new DocNgrams(); docNgrams.setFirstLocation(ngram.getFirstLocation());
			 * docNgrams.setPhrase(ngram.getPhrase()); docNgrams.setPhraseCount(ngram.getPhraseCount()); ngrams.add(docNgrams); }
			 * document.setNgrams(ngrams); }
			 */

		} catch (Exception e) {
			LOG.error("Exception Converting DocumentPOJO", e);
		}
		return document;
	}

	private String getTrimmedSummary(String summary, DocEntry doc) {
		String trimmedsummary = "";
		int bodyLength = doc.bodyLength;
		int headlineLength = doc.title.length();
		bodyLength = bodyLength - headlineLength;
		if (bodyLength > 1) {
			int trimLength = Math.min(((Float) (bodyLength * SUMMARY_TRIM_FACTOR)).intValue(), MAX_SUMMARY_LENGTH);

			if (summary.length() > trimLength) {
				trimmedsummary = summary.substring(0, trimLength);
				if (trimmedsummary.lastIndexOf(" ") > -1) {
					trimmedsummary = trimmedsummary.substring(0, trimmedsummary.lastIndexOf(" "));
				}
				LOG.debug("Summary trimmed, summary length: " + summary.length() + " bodyLength: " + bodyLength + " trimLength: "
						+ trimLength);
			} else {
				trimmedsummary = summary;
			}
			if (!trimmedsummary.endsWith(".")) {
				trimmedsummary += "...";
			}
		}
		return trimmedsummary;
	}

	public Document convertDocumentPOJOFromDocEntryObject(DocEntry doc) {
		Document document = new Document();
		try {
			document.setId(doc.getSitedocId());
			document.setTitle(doc.getTitle());
			document.setUrl(doc.getUrl());
			document.setSource(convertEntityFromEntityEntry(doc.getSourceEntity(), (short) 0));
			document.setDate(doc.getInsertTime());
		} catch (Exception e) {
			LOG.error("Exception Converting DocumentPOJO", e);
		}
		return document;
	}

	public Entity convertEntityFromEntityEntry(EntityEntry entry, short band) {
		if (entry == null) {
			return null;
		}

		Entity entity = new Entity();
		entity.setId(entry.getId());
		entity.setName(entry.getName());
		entity.setSearchToken(entry.getSearchToken());
		entity.setTicker(entry.getPrimaryTicker());
		entity.setBand(new Integer(band));
		entity.setType(entry.getType());
		return entity;
	}

	public Entity convertEntityFromEntityEntry(EntityEntry entry, short band, short relevance) {
		if (entry == null) {
			return null;
		}

		Entity entity = new Entity();
		entity.setId(entry.getId());
		entity.setName(entry.getName());
		entity.setSearchToken(entry.getSearchToken());
		entity.setTicker(entry.getPrimaryTicker());
		entity.setBand(new Integer(band));
		entity.setType(entry.getType() - SearchTokenEntry.SEARCH_TOKEN_TAG_BASE);
		entity.setRelevanceScore(relevance);
		return entity;
	}

	private int getCurrentScore(int scoreParam, Date date) {
		int score = scoreParam;
		if (score > 0) {
			float hourDiff = FR_DateUtils.timeDiffInHours(new Timestamp(System.currentTimeMillis()), new Timestamp(date.getTime()));
			if (hourDiff >= 24) {
				score = (Math.max(0, Math.min(score / 2, score - 2)));
			} else if (hourDiff >= 12 && hourDiff < 24) {
				score = (Math.max(0, score - 2));
			}
		}
		return score;
	}

	private List<Entity> getEntityPOJOList(List<DocCatEntry> docEntryList, ContentType contentType, boolean doCheck) {
		List<Entity> entityList = null;
		try {
			if (docEntryList != null) {
				entityList = new ArrayList<Entity>();
				if (doCheck) {
					for (int i = 0; i < docEntryList.size(); i++) {
						DocCatEntry dce = docEntryList.get(i);
						EntityEntry input = dce.getEntity();
						if (contentType.getId() == Integer.parseInt(input.getId())) {
							entityList.add(convertEntityFromEntityEntry(input, dce.band, dce.relevanceNormalized));
						}
					}
				} else {
					for (int i = 0; i < docEntryList.size(); i++) {
						DocCatEntry dce = docEntryList.get(i);
						EntityEntry input = dce.getEntity();
						entityList.add(convertEntityFromEntityEntry(input, dce.band, dce.relevanceNormalized));
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception Getting EntityPOJOList", e);
		}
		return entityList;
	}

	public Entity convertEntityInfo(IEntityInfo info) {
		if (info == null) {
			return null;
		}
		Entity entity = new Entity();
		try {
			entity.setId(info.getId());
			entity.setName(info.getName());
			if (info.getCompanyId() != -1) {
				entity.setCompanyId(info.getCompanyId());
			}
			entity.setSearchToken(info.getSearchToken());
			entity.setTicker(info.getPrimaryTicker());
			entity.setDocCount(info.getDocCount());
			entity.setBand(info.getScope());
			entity.setScope((short) info.getScope());
		} catch (Exception e) {
			LOG.error("Exception Converting Entity Info for catId:" + info.getId(), e);
		}
		return entity;
	}

	public DateBucketingMode convertToServiceDateBucketingMode(DateBucketingMode mode) {
		return convertToBaseDateBucketingMode(mode); 
		
	}

	public DateBucketingMode convertToBaseDateBucketingMode(DateBucketingMode mode) {
		if (mode == null) {
			return null;
		}
		DateBucketingMode pojoMode = null;
		switch (mode) {
			case AUTO:
				pojoMode = DateBucketingMode.AUTO;
				break;
			case SMART:
				pojoMode = DateBucketingMode.SMART;
				break;
			case DATE:
				pojoMode = DateBucketingMode.DATE;
				break;
			default:
				break;
		}
		return pojoMode;
	}

	public List<Event> convertToBaseType(List<IEvents> eventList, boolean ipad, boolean isSingleSearch) {
		List<Event> eventListFinal = null;
		if (eventList != null && !eventList.isEmpty()) {
			eventListFinal = new ArrayList<Event>();
			for (IEvents e : eventList) {
				eventListFinal.add(convertToBaseType(e, ipad, isSingleSearch, false));
			}
		}
		return eventListFinal;
	}

	public Event convertToBaseType(IEvents event, boolean ipad, boolean isSingleSearch, boolean isDetail) {
		Event portalEvent = new Event();
		try {
			portalEvent = copyBasicEventInfo(event);
			portalEvent.setReportDate(event.getReportDate());
			boolean isMTevent = populateTriggerAndPrimaryEvidenceAndGetMTEvent(portalEvent, event);
			MgmtChangeType changeType = null;
			if (event.getEventGroup() == EventGroupEnum.GROUP_WEB_VOLUME) {
				portalEvent.setEventInformationEnum(EventInformationEnum.WEB_VOLUME);
			} else if (event.getEventGroup() == EventGroupEnum.GROUP_8K_FILING
					|| event.getEventGroup() == EventGroupEnum.GROUP_DELAYED_FILING) {
				portalEvent.setEventInformationEnum(EventInformationEnum.SEC);
				if (isDetail) {
					SecEventMeta secMeta = new SecEventMeta();
					secMeta.setOriginalTitle(event.getProperties().get("DocTitle").toString());
					portalEvent.setSecMeta(secMeta);
				}

			} else if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) == FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_HIRE);
				changeType = MgmtChangeType.HIRE;
			} else if ((event.getFlag()
					& FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) == FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_MOVE);
				changeType = MgmtChangeType.INTERNALMOVE;
			} else if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) == FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_DEPARTURE);
				changeType = MgmtChangeType.DEPARTURE;
			} else if (event.getProperties() != null && event.getProperties().containsKey("percentChange")) {
				String change = retrieevePercentChangeAndPopulateEventInformationEnum(event, portalEvent); 
				
				if (isDetail) {
					StockPriceMeta stockPriceMeta = new StockPriceMeta();
					if (event.getProperties().containsKey("avg100Day")) {
						stockPriceMeta.setAvg100Day(event.getProperties().get("avg100Day").toString());
					}
					if (event.getProperties().containsKey("avg50Day")) {
						stockPriceMeta.setAvg50Day(event.getProperties().get("avg50Day").toString());
					}
					if (event.getProperties().containsKey("avg200Day")) {
						stockPriceMeta.setAvg200Day(event.getProperties().get("avg200Day").toString());
					}
					if (event.getProperties().containsKey("avg52Week")) {
						stockPriceMeta.setAvg52Week(event.getProperties().get("avg52Week").toString());
					}
					if (event.getProperties().containsKey("closingPrice")) {
						stockPriceMeta.setClosingPrice(event.getProperties().get("closingPrice").toString());
					}
					if (event.getProperties().containsKey("openingPrice")) {
						stockPriceMeta.setOpeningPrice(event.getProperties().get("openingPrice").toString());
					}
					stockPriceMeta.setPercentChange(change);
					portalEvent.setStockPriceMeta(stockPriceMeta);
				}
			}

			String titleType = null;
			if (ipad) {
				titleType = TitleType.HTML.name();
			}

			portalEvent.setTitle(getTitle(event, titleType, isSingleSearch));
			/*
			 * if(ipad) { portalEvent.setMobileTitle(getMobileTitle(event)); }
			 */

			Map<String, Object> properties = event.getProperties();
			portalEvent.setPerson((String) properties.get("name"));
			if (isMTevent) {
				IEntityInfo newCompanyEntity = (IEntityInfo) properties.get("newCompanyEntity");
				IEntityInfo oldCompanyEntity = (IEntityInfo) properties.get("oldCompanyEntity");
				properties.put("newCompanyEntity", this.convertEntityInfo(newCompanyEntity));
				properties.put("oldCompanyEntity", this.convertEntityInfo(oldCompanyEntity));
				if (isDetail) {
					MgmtTurnoverMeta mtMeta = new MgmtTurnoverMeta();
					mtMeta.setNewDesignation((String) properties.get("newDesignation"));
					mtMeta.setOldDesignation((String) properties.get("oldDesignation"));
					mtMeta.setNewRegion((String) properties.get("newRegion"));
					mtMeta.setOldRegion((String) properties.get("oldRegion"));
					mtMeta.setOldGroup((String) properties.get("oldGroup"));
					mtMeta.setNewGroup((String) properties.get("newGroup"));
					mtMeta.setPerson((String) properties.get("name"));
					mtMeta.setNewCompany(this.convertEntityInfo(newCompanyEntity));
					mtMeta.setOldCompany(this.convertEntityInfo(oldCompanyEntity));
					mtMeta.setChangeType(changeType);
					portalEvent.setMtMeta(mtMeta);
				}
			}

			portalEvent.setProperties(properties);
		} catch (Exception e) {
			LOG.error("Exception Processing Events!", e);
		}
		return portalEvent;
	}

	public String getTitle(IEvents event, String titleType, boolean isSingleSearch) {
		EventGroupEnum eventGroupEnum = event.getEventGroup();
		if (eventGroupEnum == EventGroupEnum.GROUP_MGMT_CHANGE) {
			MgmtChangeType changeType;
			changeType = getMgmtChangeType(event);
			Map<String, Object> prop = event.getProperties();
			String newCompany = null;
			IEntityInfo e = (IEntityInfo) prop.get("newCompanyEntity");
			if (e != null) {
				newCompany = e.getName();
			}
			String oldCompany = null;
			e = (IEntityInfo) prop.get("oldCompanyEntity");
			if (e != null) {
				oldCompany = e.getName();
			}
			Boolean encoded = (Boolean) prop.get("encoded");
			if (encoded == null) {
				encoded = Boolean.FALSE;
			}

			String title;
			// CHECK encoded
			if (titleType != null && titleType.equals(TitleType.HTML.name())) {
				if (isSingleSearch) {

					title = TitleUtils.getMgmtTurnoverTitleInlineHtml(changeType, newCompany, (String) prop.get("name"),
							(String) prop.get("newDesignation"), (String) prop.get("newGroup"), (String) prop.get("newRegion"), oldCompany,
							(String) prop.get("oldDesignation"), (String) prop.get("oldGroup"), (String) prop.get("oldRegion"), false);
				} else {
					title = TitleUtils.getMgmtTurnoverTitleInlineHtml(changeType, newCompany, (String) prop.get("name"),
							(String) prop.get("newDesignation"), (String) prop.get("newGroup"), (String) prop.get("newRegion"), oldCompany,
							(String) prop.get("oldDesignation"), (String) prop.get("oldGroup"), (String) prop.get("oldRegion"), true);
				}
			} else {
				title = TitleUtils.getMgmtTurnoverTitle(changeType, newCompany, (String) prop.get("name"),
						(String) prop.get("newDesignation"), (String) prop.get("newGroup"), (String) prop.get("newRegion"), oldCompany,
						(String) prop.get("oldDesignation"), (String) prop.get("oldGroup"), (String) prop.get("oldRegion"), false,
						!encoded);
			}
			return title;
		} else if (eventGroupEnum == EventGroupEnum.GROUP_STOCK_PRICE) {
			int eventType = event.getEventType().getId();
			Map<String, Object> prop = event.getProperties();
			// set ipad title
			String title = null;
			if (titleType != null && titleType.equals(TitleType.HTML.name()) && !isSingleSearch) {
				title = TitleUtils.getSPTitleWithHtml(eventType, event.getEntityInfo().getName(), prop.get("avg50Day").toString(),
						prop.get("avg100Day").toString(), prop.get("avg200Day").toString(), prop.get("avg52Week").toString(),
						prop.get("closingPrice").toString(), prop.get("percentChange").toString());
			} else {
				title = TitleUtils.getSPTitle(eventType, null, prop.get("avg50Day").toString(), prop.get("avg100Day").toString(),
						prop.get("avg200Day").toString(), prop.get("avg52Week").toString(), prop.get("closingPrice").toString(),
						prop.get("percentChange").toString());
			}
			return title;
		} else if (eventGroupEnum == EventGroupEnum.GROUP_8K_FILING || eventGroupEnum == EventGroupEnum.GROUP_DELAYED_FILING) {
			String title = event.getProperties().get("DocTitle").toString();
			int eventType = event.getEventType().getId();
			if (titleType != null && titleType.equals(TitleType.HTML.name())) {
				return EventUtils.getSecTitle(null, title, eventType, TitleType.HTML);
			} else {
				return EventUtils.getSecTitle(null, title, eventType);
			}

		} else {
			return (String) event.getProperties().get("title");
		}
	}

	public List<Event> convertToBaseTypeForGraph(List<IEvents> eventList, boolean isSignal) {
		List<Event> eventListFinal = new ArrayList<Event>();
		if (eventList != null && !eventList.isEmpty()) {
			for (IEvents e : eventList) {
				eventListFinal.add(convertToBaseTypeForGraph(e, isSignal));
			}
		}
		return eventListFinal;
	}

	private Event convertToBaseTypeForGraph(IEvents event, boolean isSignal) {
		Event portalEvent = new Event();
		String title = "";
		try {
			portalEvent = copyBasicEventInfo(event);
			boolean isMTevent = populateTriggerAndPrimaryEvidenceAndGetMTEvent(portalEvent, event);
			if (event.getEventGroup() == EventGroupEnum.GROUP_WEB_VOLUME) {
				portalEvent.setEventInformationEnum(EventInformationEnum.WEB_VOLUME);
			} else if (event.getEventGroup() == EventGroupEnum.GROUP_8K_FILING
					|| event.getEventGroup() == EventGroupEnum.GROUP_DELAYED_FILING) {
				portalEvent.setEventInformationEnum(EventInformationEnum.SEC);
			} else if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) == FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_HIRE);
			} else if ((event.getFlag()
					& FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) == FR_IEventEntity.FLAG_BITMAP_MASK_INTERNAL_MOVE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_MOVE);
			} else if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) == FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) {
				isMTevent = true;
				portalEvent.setEventInformationEnum(EventInformationEnum.MT_DEPARTURE);
			} else if (event.getProperties() != null && event.getProperties().containsKey("percentChange")) {
				String change = retrieevePercentChangeAndPopulateEventInformationEnum(event, portalEvent); 
				
			}

			if (event.getEventGroup() == EventGroupEnum.GROUP_STOCK_PRICE) {
				Map<String, Object> prop = event.getProperties();

				String companyName = null;
				if (event.getEntityInfo() != null) {
					companyName = event.getEntityInfo().getName();
				}

				if (isSignal) {
					title = TitleUtils.getSPTitleWithoutHtml(event.getEventType().getId(), companyName, prop.get("avg50Day").toString(),
							prop.get("avg100Day").toString(), prop.get("avg200Day").toString(), prop.get("avg52Week").toString(),
							prop.get("closingPrice").toString(), prop.get("percentChange").toString());
				} else {
					title = TitleUtils.getSPTitle(event.getEventType().getId(), companyName, prop.get("avg50Day").toString(),
							prop.get("avg100Day").toString(), prop.get("avg200Day").toString(), prop.get("avg52Week").toString(),
							prop.get("closingPrice").toString(), prop.get("percentChange").toString());
				}

			} else if (event.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE) {
				MgmtChangeType changeType;
				changeType = getMgmtChangeType(event);
				Map<String, Object> prop = event.getProperties();
				String newCompany = null;
				IEntityInfo e = (IEntityInfo) prop.get("newCompanyEntity");
				if (e != null) {
					newCompany = e.getName();
				}
				String oldCompany = null;
				e = (IEntityInfo) prop.get("oldCompanyEntity");
				if (e != null) {
					oldCompany = e.getName();
				}
				if (isSignal) {
					title = TitleUtils.getMgmtTurnoverTitleWithoutHtml(changeType, newCompany, (String) prop.get("name"),
							(String) prop.get("newDesignation"), (String) prop.get("newGroup"), (String) prop.get("newRegion"), oldCompany,
							(String) prop.get("oldDesignation"), (String) prop.get("oldGroup"), (String) prop.get("oldRegion"));
				} else {
					title = TitleUtils.getMgmtTurnoverTitle(changeType, newCompany, (String) prop.get("name"),
							(String) prop.get("newDesignation"), (String) prop.get("newGroup"), (String) prop.get("newRegion"), oldCompany,
							(String) prop.get("oldDesignation"), (String) prop.get("oldGroup"), (String) prop.get("oldRegion"), false);
				}

			} else if (event.getEventGroup() == EventGroupEnum.GROUP_8K_FILING) {
				title = event.getProperties().get("DocTitle").toString();
				if (event.getEventType().getId() == FR_IEvent.TYPE_TEN_K_FILING) {
					title = getTitle(isSignal, title, "10-K");
				} else if (event.getEventType().getId() == FR_IEvent.TYPE_TEN_Q_FILING) {
					title = getTitle(isSignal, title, "10-Q");
				} else {
					title = event.getCaption();
				}
			} else {
				title = event.getCaption();
			}
			portalEvent.setTitle(title);

			Map<String, Object> properties = event.getProperties();

			if (isMTevent) {
				IEntityInfo newCompanyEntity = (IEntityInfo) properties.get("newCompanyEntity");
				IEntityInfo oldCompanyEntity = (IEntityInfo) properties.get("oldCompanyEntity");
				properties.put("newCompanyEntity", this.convertEntityInfo(newCompanyEntity));
				properties.put("oldCompanyEntity", this.convertEntityInfo(oldCompanyEntity));
			}

		} catch (Exception e) {
			LOG.error("Exception Processing Events!", e);
		}
		return portalEvent;
	}

	private String getTitle(final boolean isSignal, String title, final String formType) {
		if (isSignal) {
			title = TitleUtils.getSecTitleWithoutHtml(null, title, formType);
		} else {
			title = TitleUtils.getSecTitle(null, title, formType);
		}
		return title;
	}

	private MgmtChangeType getMgmtChangeType(final IEvents event) {
		MgmtChangeType changeType = null;
		if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) == FR_IEventEntity.FLAG_BITMAP_MASK_HIRE) {
			changeType = MgmtChangeType.HIRE;
		} else if ((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) == FR_IEventEntity.FLAG_BITMAP_MASK_DEPARTURE) {
			changeType = MgmtChangeType.DEPARTURE;
		} else {
			changeType = MgmtChangeType.INTERNALMOVE;
		}
		return changeType;
	}

	private boolean populateTriggerAndPrimaryEvidenceAndGetMTEvent(final Event portalEvent, final IEvents event) {
		portalEvent.setTrigger((event.getFlag()
				& FR_IEventEntity.FLAG_BITMAP_MASK_TRIGGER) == FR_IEventEntity.FLAG_BITMAP_MASK_TRIGGER);
		
		// NOTE:Casting to real implementation is just working(although its bad) as we don't
		// have multiple implementations yet.
		// Or it should work on explicit types only and keep the method un-exposed.
		Set<Long> primaryEvidenceEntityIds = ((EventObj) event).getPrimaryEvidenceEntityIds();
		if (primaryEvidenceEntityIds != null) {
			portalEvent.setPrimaryEvidenceEntityIds(primaryEvidenceEntityIds);
		}
		boolean isMTevent = false;
		return isMTevent;
	}
 
	private String retrieevePercentChangeAndPopulateEventInformationEnum(final IEvents event, final Event portalEvent) { 
		String change = null; 
		change = "" + event.getProperties().get("percentChange"); 
		if (change.startsWith("-")) { 
			portalEvent.setEventInformationEnum(EventInformationEnum.PRICE_DOWN); 
		} else { 
			portalEvent.setEventInformationEnum(EventInformationEnum.PRICE_UP); 
		} 
		return change; 
	} 
	

	private Event copyBasicEventInfo(IEvents event) {
		Event portalEvent = new Event();
		portalEvent.setCaption(event.getCaption());
		portalEvent.setDate(event.getDate());
		portalEvent.setDescription(event.getDescription());
		portalEvent.setEntityId(event.getEntityId());
		portalEvent.setEntityInfo(convertEntityInfo(event.getEntityInfo()));
		portalEvent.setHasExpired(event.hasExpired());
		portalEvent.setEventId(event.getEventId() + "");
		portalEvent.setEventType(event.getEventType().name());
		portalEvent.setEventTypeId(event.getEventType().getId());
		portalEvent.setFlag(event.getFlag());
		portalEvent.setLinkable(((EventObj) event).isLinkable());
		portalEvent.setScore(((EventObj) event).getScore());
		portalEvent.setUrl(((EventObj) event).getUrl());
		portalEvent.setDayId(((EventObj) event).getDayId());
		return portalEvent;
	}

	/**
	 * Scans expired events and attach indexed summary to description, matched companies and topics to event properties, if available.
	 * <p>
	 * Consider web volume type events only.
	 * 
	 * @param searcher
	 * @param events
	 */
	public void attachPropertiesForExpiredEvents(SolrSearcher searcher, List<Event> events) {
		// List<Event> eventsOutput = new ArrayList<Event>();
		if (events == null || events.isEmpty()) {
			return;
		}

		if (searcher == null) {
			throw new IllegalArgumentException("Either {searcher} or {events} is null.");
		}

		// For caching sitedocs for further usage
		Map<Long, DocEntry> docEntriesMap = new HashMap<Long, DocEntry>();
		for (Event event : events) {
			Event eventObj = event;
			if (!event.isHasExpired()) {
				continue;
			}
			EventInformationEnum eventType = event.getEventInformationEnum();
			Set<Long> evidenceEntityIds = eventObj.getPrimaryEvidenceEntityIds();
			boolean evidenceAvailable = evidenceEntityIds != null && !evidenceEntityIds.isEmpty();
			if (!evidenceAvailable) {
				continue;
			}
			// Handle different event types separately
			Long entityId = -1L;
			if (eventType == EventInformationEnum.WEB_VOLUME) {

				entityId = evidenceEntityIds.iterator().next();
				DocEntry doc = docEntriesMap.get(entityId);
				if (doc == null) {
					try {
						List<DocEntry> docs = searcher.fetch(entityId);
						if (docs != null && !docs.isEmpty()) {
							doc = docs.get(0);
							docEntriesMap.put(entityId, doc);
						}
					} catch (SearchException e) {
						LOG.error("Failed to attach expired event properties for eventId: " + event.getEventId() + " sitedocid: " + entityId
								+ " due to " + e.getMessage(), e);
						continue;
					}
				}
				attachAdditionlPropertyToEvent(event, doc);
			}
		}
		docEntriesMap.clear();
	}

	private void attachAdditionlPropertyToEvent(Event eventObj, DocEntry doc) {
		if (doc == null) {
			return;
		}
		eventObj.setDescription(doc.summary);
		List<DocCatEntry> companies = doc.matchedCompanies;
		List<DocCatEntry> topics = doc.matchedTopics;
		eventObj.setMatchedCompanies(getEntityPOJOList(companies, null, false));
		eventObj.setMatchedTopics(getEntityPOJOList(topics, null, false));
	}

	public List<Event> convertToBaseType(List<IEvents> eventList) {
		return convertToBaseType(eventList, false, false);
	}

}
