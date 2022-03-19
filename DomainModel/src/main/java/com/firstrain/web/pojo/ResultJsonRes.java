/**
 * 
 */
package com.firstrain.web.pojo;

/**
 * @author vgupta
 *
 */
public class ResultJsonRes extends Entity {
	private Long catId;
	private String docId;
	private String title;
	private String body;

	public Long getCatId() {
		return catId;
	}

	public void setCatId(Long catId) {
		this.catId = catId;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
