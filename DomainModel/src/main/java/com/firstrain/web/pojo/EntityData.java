package com.firstrain.web.pojo;


import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.domain.MonitorEmail;
import com.firstrain.web.wrapper.ChartDataWrapper;
import com.firstrain.web.wrapper.EntityMap;

@XmlRootElement(name = "data")
public class EntityData {
	private Content fr;
	private Content ft;
	private Content ac;
	private Content bucketedEvents;
	private Content e;
	private Content te;
	private Content cs;
	private WebVolumeGraph wv;
	private ChartDataWrapper twt;
	private ChartDataWrapper bi;
	private ChartDataWrapper md;
	private ChartDataWrapper gl;
	private ChartDataWrapper rl;
	private ChartDataWrapper tt;
	private String emailTemplate;
	private EmailDetail emailDetail;
	private List<MonitorEmail> emails;
	private Integer totalItemCount;
	private Integer itemOffset;
	private Integer itemCount;
	private List<Document> conversationStarters;
	private EntityMap entityMap;
	private List<EntityStandard> entity;
	private List<EntityStandard> peers;
	private String scopeDirective;

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public EmailDetail getEmailDetail() {
		return emailDetail;
	}

	public void setEmailDetail(EmailDetail emailDetail) {
		this.emailDetail = emailDetail;
	}

	public List<MonitorEmail> getEmails() {
		return emails;
	}

	public void setEmails(List<MonitorEmail> emails) {
		this.emails = emails;
	}

	public Content getFr() {
		return fr;
	}

	public void setFr(Content fr) {
		this.fr = fr;
	}

	public Content getFt() {
		return ft;
	}

	public void setFt(Content ft) {
		this.ft = ft;
	}

	public ChartDataWrapper getTwt() {
		return twt;
	}

	public void setTwt(ChartDataWrapper twt) {
		this.twt = twt;
	}

	public ChartDataWrapper getBi() {
		return bi;
	}

	public void setBi(ChartDataWrapper bi) {
		this.bi = bi;
	}

	public ChartDataWrapper getMd() {
		return md;
	}

	public void setMd(ChartDataWrapper md) {
		this.md = md;
	}

	public ChartDataWrapper getGl() {
		return gl;
	}

	public void setGl(ChartDataWrapper gl) {
		this.gl = gl;
	}

	public ChartDataWrapper getRl() {
		return rl;
	}

	public void setRl(ChartDataWrapper rl) {
		this.rl = rl;
	}

	public ChartDataWrapper getTt() {
		return tt;
	}

	public void setTt(ChartDataWrapper tt) {
		this.tt = tt;
	}

	public Content getAc() {
		return ac;
	}

	public void setAc(Content ac) {
		this.ac = ac;
	}

	public Content getE() {
		return e;
	}

	public void setE(Content e) {
		this.e = e;
	}

	public Content getTe() {
		return te;
	}

	public void setTe(Content te) {
		this.te = te;
	}

	@JsonIgnore
	public Content getCs() {
		return cs;
	}

	public void setCs(Content cs) {
		this.cs = cs;
	}

	public WebVolumeGraph getWv() {
		return wv;
	}

	public void setWv(WebVolumeGraph wv) {
		this.wv = wv;
	}

	@JsonIgnore
	public Content getBucketedEvents() {
		return bucketedEvents;
	}

	public void setBucketedEvents(Content bucketedEvents) {
		this.bucketedEvents = bucketedEvents;
	}

	public Integer getTotalItemCount() {
		return totalItemCount;
	}

	public void setTotalItemCount(Integer totalItemCount) {
		this.totalItemCount = totalItemCount;
	}

	public Integer getItemOffset() {
		return itemOffset;
	}

	public void setItemOffset(Integer itemOffset) {
		this.itemOffset = itemOffset;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public List<Document> getConversationStarters() {
		return conversationStarters;
	}

	public void setConversationStarters(List<Document> conversationStarters) {
		this.conversationStarters = conversationStarters;
	}

	public EntityMap getEntityMap() {
		return this.entityMap;
	}

	public void setEntityMap(EntityMap entityMap) {
		this.entityMap = entityMap;
	}

	public List<EntityStandard> getEntity() {
		return entity;
	}

	public void setEntity(List<EntityStandard> entity) {
		this.entity = entity;
	}

	public List<EntityStandard> getPeers() {
		return peers;
	}

	public void setPeers(List<EntityStandard> peers) {
		this.peers = peers;
	}

	public String getScopeDirective() {
		return scopeDirective;
	}

	public void setScopeDirective(String scopeDirective) {
		this.scopeDirective = scopeDirective;
	}
}
