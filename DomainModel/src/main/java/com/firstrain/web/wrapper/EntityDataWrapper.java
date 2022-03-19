package com.firstrain.web.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.MetaData;

@XmlRootElement(name = "data")
public class EntityDataWrapper {
	private String name;
	private String id;
	private EntityData data;
	private MetaData metaData;
	private EntityDataHtml htmlFrag;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EntityData getData() {
		return data;
	}

	public void setData(EntityData data) {
		this.data = data;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public EntityDataHtml getHtmlFrag() {
		return htmlFrag;
	}

	public void setHtmlFrag(EntityDataHtml htmlFrag) {
		this.htmlFrag = htmlFrag;
	}
}
