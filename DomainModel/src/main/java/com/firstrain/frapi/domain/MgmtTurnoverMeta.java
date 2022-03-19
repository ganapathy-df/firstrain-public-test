package com.firstrain.frapi.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.utils.FR_Encoder;
import com.firstrain.utils.MgmtChangeType;
import com.firstrain.frapi.pojo.Entity;

public class MgmtTurnoverMeta extends MetaEvent {

	private String trendId;
	private Entity newCompany;
	private Entity oldCompany;
	private String newDesignation;
	private String oldDesignation;
	private String person;
	private MgmtChangeType changeType;
	private String oldRegion;
	private String newRegion;
	private String oldGroup;
	private String newGroup;
	private boolean futureEvent = false;

	public String getTrendId() {
		return trendId;
	}

	public void setTrendId(String trendId) {
		this.trendId = trendId;
	}

	public Entity getNewCompany() {
		return newCompany;
	}

	public void setNewCompany(Entity newCompany) {
		this.newCompany = newCompany;
	}

	public Entity getOldCompany() {
		return oldCompany;
	}

	public void setOldCompany(Entity oldCompany) {
		this.oldCompany = oldCompany;
	}

	public String getNewDesignation() {
		return newDesignation;
	}

	public void setNewDesignation(String newDesignation) {
		this.newDesignation = newDesignation;
	}

	public String getOldDesignation() {
		return oldDesignation;
	}

	public void setOldDesignation(String oldDesignation) {
		this.oldDesignation = oldDesignation;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public MgmtChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(MgmtChangeType changeType) {
		this.changeType = changeType;
	}

	public String getOldRegion() {
		return oldRegion;
	}

	public void setOldRegion(String oldRegion) {
		this.oldRegion = oldRegion;
	}

	public String getNewRegion() {
		return newRegion;
	}

	public void setNewRegion(String newRegion) {
		this.newRegion = newRegion;
	}

	public String getOldGroup() {
		return oldGroup;
	}

	public void setOldGroup(String oldGroup) {
		this.oldGroup = oldGroup;
	}

	public String getNewGroup() {
		return newGroup;
	}

	public void setNewGroup(String newGroup) {
		this.newGroup = newGroup;
	}

	public boolean isFutureEvent() {
		return futureEvent;
	}

	public void setFutureEvent(boolean futureEvent) {
		this.futureEvent = futureEvent;
	}

	private static String FONT_GREEN = "<span style='color:#32932d; text-transform:uppercase;font-size:90%'>";
	private static String FONT_BLUE = "<span style='color:#005596; text-transform:uppercase;font-size:90%'>";
	private static String FONT_RED = "<span style='color:#e82d2d; text-transform:uppercase;font-size:90%'>";

	@JsonIgnore
	public String getMgmtTurnoverTitleWithoutHtml() {

		StringBuilder title = new StringBuilder();
		switch (changeType) {
			case HIRE:
				title.append(person);
				title.append(" ").append("JOINED");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append(" as ").append(FR_Encoder.encodeXML(newDesignation, FR_Encoder.CHARACTER_ENCODING));
					appendEncodedNewGroupRegion(newGroup, title, newRegion); 
					
				}
				if (oldCompany != null && oldCompany.getName() != null && !oldCompany.getName().isEmpty()) {
					title.append(" from ").append(FR_Encoder.encodeXML(oldCompany.getName(), FR_Encoder.CHARACTER_ENCODING));
				}
				break;
			case DEPARTURE:
				title.append(person);
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(FR_Encoder.encodeXML(oldDesignation, FR_Encoder.CHARACTER_ENCODING));
					appendEncodedNewGroupRegion(oldGroup, title, oldRegion); 
					
				}
				title.append(" ").append("LEFT ");
				if (newCompany != null && newCompany.getName() != null && !newCompany.getName().isEmpty()) {
					title.append("for ").append(FR_Encoder.encodeXML(newCompany.getName(), FR_Encoder.CHARACTER_ENCODING));
				}
				break;
			case INTERNALMOVE:
				title.append(person);
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(FR_Encoder.encodeXML(oldDesignation, FR_Encoder.CHARACTER_ENCODING));
					appendEncodedNewGroupRegion(oldGroup, title, oldRegion); 
					
				}
				title.append(" ").append("MOVED ");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append("to ").append(FR_Encoder.encodeXML(newDesignation, FR_Encoder.CHARACTER_ENCODING));
					appendEncodedNewGroupRegion(newGroup, title, newRegion); 
					
				}
				break;
			default:
				break;
		}
		return title.toString();
	}
 
	private void appendEncodedNewGroupRegion(final String newGroup, final StringBuilder title, final String newRegion) { 
		if (newGroup != null && !newGroup.isEmpty()) { 
			title.append(" of ").append(FR_Encoder.encodeXML(newGroup, FR_Encoder.CHARACTER_ENCODING)); 
		} 
		if (newRegion != null && !newRegion.isEmpty()) { 
			title.append(", ").append(FR_Encoder.encodeXML(newRegion, FR_Encoder.CHARACTER_ENCODING)); 
		} 
	} 
	

	@JsonIgnore
	public String getMgmtTurnoverTitle() {
		boolean encode = true;
		StringBuilder title = new StringBuilder();
		switch (changeType) {
			case HIRE:
				/*
				 * if(showCompany && newCompany != null && !newCompany.isEmpty()) { title.append(inlineStyle ? S_ORANGE_INLINESTYLE :
				 * S_ORANGE) .append(getRefinedStr(newCompany, encode)) .append("</span> <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				title.append(" ").append("<span class='status-callout mgmt-status--joined'>").append("JOINED</span>");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append(" as ").append(getRefinedStr(newDesignation, encode));
					appendRefinedOldGroupRegion(newGroup, title, encode, newRegion); 
					
				}
				if (oldCompany != null && oldCompany.getName() != null && !oldCompany.getName().isEmpty()) {
					title.append(" from ").append(getRefinedStr(oldCompany.getName(), encode));
				}
				break;
			case DEPARTURE:
				/*
				 * if(showCompany && oldCompany != null && !oldCompany.isEmpty()) { title.append(inlineStyle ? S_ORANGE_INLINESTYLE :
				 * S_ORANGE) .append(getRefinedStr(oldCompany, encode)) .append("</span> <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(getRefinedStr(oldDesignation, encode));
					appendRefinedOldGroupRegion(oldGroup, title, encode, oldRegion); 
					
				}
				title.append(" ").append("<span class='status-callout mgmt-status--left'>").append("LEFT</span></span> ");
				if (newCompany != null && newCompany.getName() != null && !newCompany.getName().isEmpty()) {
					title.append("for ").append(getRefinedStr(newCompany.getName(), encode));
				}
				break;
			case INTERNALMOVE:
				/*
				 * if(showCompany && oldCompany != null && !oldCompany.isEmpty()) { title.append(inlineStyle ? S_ORANGE_INLINESTYLE :
				 * S_ORANGE) .append(getRefinedStr(oldCompany, encode)) .append("</span> <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(getRefinedStr(oldDesignation, encode));
					appendRefinedOldGroupRegion(oldGroup, title, encode, oldRegion); 
					
				}
				title.append(" ").append("<span class='status-callout mgmt-status--move'>").append("MOVED</span></span> ");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append("to ").append(getRefinedStr(newDesignation, encode));
					appendRefinedOldGroupRegion(newGroup, title, encode, newRegion); 
					
				}
				break;
			default:
				break;
		}
		return title.toString();
	}
 
	private void appendRefinedOldGroupRegion(final String oldGroup, final StringBuilder title, final boolean encode, final String oldRegion) { 
		if (oldGroup != null && !oldGroup.isEmpty()) { 
			title.append(" of ").append(getRefinedStr(oldGroup, encode)); 
		} 
		if (oldRegion != null && !oldRegion.isEmpty()) { 
			title.append(", ").append(getRefinedStr(oldRegion, encode)); 
		} 
	} 
	

	@JsonIgnore
	public String getMgmtTurnoverTitleInlineHtml() {

		StringBuilder title = new StringBuilder();
		switch (changeType) {
			case HIRE:
				/*
				 * if(showCompany && newCompany != null && !newCompany.isEmpty()) { title.append(FONT_ORANGE) .append(newCompany)
				 * .append(FONT_CLOSE) .append(" <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				title.append(" ").append(FONT_GREEN).append("JOINED").append("</span>").append(" ");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append(" as ").append(newDesignation);
					appendNewGroupRegion(newGroup, title, newRegion); 
					
				}
				if (oldCompany != null && oldCompany.getName() != null && !oldCompany.getName().isEmpty()) {
					title.append(" from ").append(oldCompany);
				}
				break;
			case DEPARTURE:
				/*
				 * if(showCompany && oldCompany != null && !oldCompany.getName().isEmpty()) { title.append(FONT_ORANGE) .append(oldCompany)
				 * .append(FONT_CLOSE) .append(" <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(oldDesignation);
					appendNewGroupRegion(oldGroup, title, oldRegion); 
					
				}
				title.append(" ").append(FONT_RED).append("LEFT").append("</span>").append(" ");
				if (newCompany != null && newCompany.getName() != null && !newCompany.getName().isEmpty()) {
					title.append("for ").append(newCompany);
				}
				break;
			case INTERNALMOVE:
				/*
				 * if(showCompany && oldCompany != null && !oldCompany.isEmpty()) { title.append(FONT_ORANGE) .append(oldCompany)
				 * .append(FONT_CLOSE) .append(" <br/>"); }
				 */
				title.append("<strong>").append(person).append("</strong>");
				if (oldDesignation != null && !oldDesignation.isEmpty()) {
					title.append(", ").append(oldDesignation);
					appendNewGroupRegion(oldGroup, title, oldRegion); 
					
				}
				title.append(" ").append("<strong>").append(FONT_BLUE).append("MOVED");
				title.append("<strong>").append(" ");
				if (newDesignation != null && !newDesignation.isEmpty()) {
					title.append("to ").append(newDesignation);
					appendNewGroupRegion(newGroup, title, newRegion); 
					
				}
				break;
			default:
				break;
		}
		return title.toString();
	}
 
	private void appendNewGroupRegion(final String newGroup, final StringBuilder title, final String newRegion) { 
		if (newGroup != null && !newGroup.isEmpty()) { 
			title.append(" of ").append(newGroup); 
		} 
		if (newRegion != null && !newRegion.isEmpty()) { 
			title.append(", ").append(newRegion); 
		} 
	} 
	

	private String getRefinedStr(String s, boolean encode) {
		if (encode) {
			return FR_Encoder.encodeXML(s, FR_Encoder.CHARACTER_ENCODING);
		}
		return s;
	}
}
