package com.firstrain.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfig {
	@XmlElements(@XmlElement(name = "enterprise", type = EnterpriseConfig.class))
	private List<EnterpriseConfig> enterprises;
	private static final Logger LOG = Logger.getLogger(ProjectConfig.class);

	public List<EnterpriseConfig> getEnterprises() {
		return enterprises;
	}

	public void setEnterprises(List<EnterpriseConfig> enterprises) {
		this.enterprises = enterprises;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EnterpriseConfig {
		@XmlAttribute
		private Long id;
		private String accesstoken;
		private String appname;
		private List<Integer> topicdimensionList;
		private String iprange;
		private short throttle;
		private short indclassid;

		public String getAccesstoken() {
			return accesstoken;
		}

		public void setAccesstoken(String accesstoken) {
			this.accesstoken = accesstoken;
		}

		public List<Integer> getTopicdimensionList() {
			return topicdimensionList;
		}

		@XmlElement(name = "topicdimensions")
		public void setTopicdimensions(String topicdimensions) throws Exception {
			this.topicdimensionList = csvToIntegerList(topicdimensions);
		}

		public short getThrottle() {
			return throttle;
		}

		public void setThrottle(short throttle) {
			this.throttle = throttle;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getAppname() {
			return appname;
		}

		public void setAppname(String appname) {
			this.appname = appname;
		}

		public String getIprange() {
			return iprange;
		}

		public void setIprange(String iprange) {
			this.iprange = iprange;
		}

		public short getIndclassid() {
			return indclassid;
		}

		public void setIndclassid(short indclassid) {
			this.indclassid = indclassid;
		}

		private ArrayList<Integer> csvToIntegerList(String csv) throws Exception {
			ArrayList<Integer> arrList = null;
			if (csv != null && csv.trim().length() > 0) {
				String strArr[] = csv.split(",");
				if (strArr != null) {
					arrList = new ArrayList<Integer>(strArr.length);
					for (int i = 0; i < strArr.length; i++) {
						try {
							arrList.add(Integer.valueOf(strArr[i].trim()));
						} catch (Exception e) {
							LOG.error("Error parsing topic dimensions: " + e.getMessage(), e);
							throw e;
						}
					}
				}
			}
			return arrList;
		}
	}
}
