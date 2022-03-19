package com.firstrain.web.wrapper;

import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.ItemData;
import com.firstrain.web.pojo.MetaData;


public class ItemWrapper {

	private ItemData data;
	private MetaData metaData;
	private EntityDataHtml htmlFrag;

	public ItemData getData() {
		return data;
	}

	public void setData(ItemData data) {
		this.data = data;
	}

	public MetaData getMetaData() {
		return this.metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public EntityDataHtml getHtmlFrag() {
		return this.htmlFrag;
	}

	public void setHtmlFrag(EntityDataHtml htmlFrag) {
		this.htmlFrag = htmlFrag;
	}

}
