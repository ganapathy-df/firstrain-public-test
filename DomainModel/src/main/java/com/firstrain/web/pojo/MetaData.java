package com.firstrain.web.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "data")
public class MetaData {
	private String fr;
	private String ft;
	private String e;
	private String visualisation;
	private String twt;
	private String bi;
	private String md;
	private String gl;
	private String rl;
	private String tt;
	private String document;
	private String tweet;
}
