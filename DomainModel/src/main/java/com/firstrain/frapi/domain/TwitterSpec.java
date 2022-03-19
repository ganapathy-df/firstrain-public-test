package com.firstrain.frapi.domain;

public class TwitterSpec extends BaseSpec {

	public TwitterSpec() {
		super();
	}

	private int rows = 30;
	private String[] catIds;
	private boolean listView = true;

	public boolean isListView() {
		return listView;
	}

	public int getRows() {
		return rows;
	}

	public String[] getCatIds() {
		return catIds;
	}

	@Override
	public Integer getScope() {
		if (super.getScope() == null) {
			return -1;
		}
		return super.getScope();
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setCatIds(String[] catIds) {
		this.catIds = catIds;
	}

	public void setListView(boolean listView) {
		this.listView = listView;
	}
}
