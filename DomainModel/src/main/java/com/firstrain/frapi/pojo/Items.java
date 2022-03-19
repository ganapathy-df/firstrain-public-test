package com.firstrain.frapi.pojo;

public class Items {

	private Long id;
	private String name;
	private String q;
	private String fq;
	private Integer scope;
	private String data;
	private Entity entity;
	private Integer docCount;

	public Integer getDocCount() {
		return docCount;
	}

	public void setDocCount(Integer docCount) {
		this.docCount = docCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getFq() {
		return fq;
	}

	public void setFq(String fq) {
		this.fq = fq;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
