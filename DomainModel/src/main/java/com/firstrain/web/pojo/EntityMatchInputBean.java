package com.firstrain.web.pojo;



public class EntityMatchInputBean {
	private EntityInput company;
	private EntityInput topic;
	private EntityInput industry;
	private EntityInput region;
	private int count;

	public EntityInput getCompany() {
		return company;
	}

	public void setCompany(EntityInput company) {
		this.company = company;
	}

	public EntityInput getTopic() {
		return topic;
	}

	public void setTopic(EntityInput topic) {
		this.topic = topic;
	}

	public EntityInput getIndustry() {
		return industry;
	}

	public void setIndustry(EntityInput industry) {
		this.industry = industry;
	}

	public EntityInput getRegion() {
		return region;
	}

	public void setRegion(EntityInput region) {
		this.region = region;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public static class EntityInput {
		private String name;
		private String duns;
		private String ticker;
		private String cikCode;
		private String sedol;
		private String isin;
		private String valeron;
		private String homePage;
		private String address;
		private String country;
		private String state;
		private String city;
		private String zip;
		private String hemscottIC;
		private String hooversIC;
		private String sic;
		private String naics;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDuns() {
			return duns;
		}

		public void setDuns(String duns) {
			this.duns = duns;
		}

		public String getTicker() {
			return ticker;
		}

		public void setTicker(String ticker) {
			this.ticker = ticker;
		}

		public String getCikCode() {
			return cikCode;
		}

		public void setCikCode(String cikCode) {
			this.cikCode = cikCode;
		}

		public String getSedol() {
			return sedol;
		}

		public void setSedol(String sedol) {
			this.sedol = sedol;
		}

		public String getIsin() {
			return isin;
		}

		public void setIsin(String isin) {
			this.isin = isin;
		}

		public String getValeron() {
			return valeron;
		}

		public void setValeron(String valeron) {
			this.valeron = valeron;
		}

		public String getHomePage() {
			return homePage;
		}

		public void setHomePage(String homePage) {
			this.homePage = homePage;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public String getHemscottIC() {
			return hemscottIC;
		}

		public void setHemscottIC(String hemscottIC) {
			this.hemscottIC = hemscottIC;
		}

		public String getHooversIC() {
			return hooversIC;
		}

		public void setHooversIC(String hooversIC) {
			this.hooversIC = hooversIC;
		}

		public String getSic() {
			return sic;
		}

		public void setSic(String sic) {
			this.sic = sic;
		}

		public String getNaics() {
			return naics;
		}

		public void setNaics(String naics) {
			this.naics = naics;
		}
	}
}
