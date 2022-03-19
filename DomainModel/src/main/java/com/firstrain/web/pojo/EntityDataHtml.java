package com.firstrain.web.pojo;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

@Setter
@Getter
@XmlRootElement(name = "data")
public class EntityDataHtml {
	private String fr;
	private String ft;
	private String e;
	private String twt;
	private String bi;
	private String md;
	private String gl;
	private String rl;
	private String tt;
	private String wv;
	private Boolean frContentBi;
	private Boolean frContentMd;
	private Boolean frContentGl;
	private Boolean frContentRl;
	private Boolean frContentTwt;
	private Boolean frContentTt;
	private Boolean frContentWv;

	@JsonIgnore
	private String analyticsRibbon;

	@JsonIgnore
	private String mgmtTurnoverChart;

	private String document;
	private String tweet;
}
