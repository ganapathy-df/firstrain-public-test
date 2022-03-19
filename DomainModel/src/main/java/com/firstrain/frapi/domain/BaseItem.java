package com.firstrain.frapi.domain;

import java.util.List;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.util.ContentType;

public class BaseItem {

	protected String id;
	protected String title;
	protected List<Entity> matchedCompanies;
	protected List<Entity> matchedTopics;
	protected ContentType contentType = ContentType.WEBNEWS;
	protected transient MetaShare visibility = null;
	protected Entity primaryEntity;
	protected Integer rank;
	protected Integer slot;

}
