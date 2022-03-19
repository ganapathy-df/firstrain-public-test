package com.firstrain.web.pojo;

import java.util.List;
import java.util.Map;

public class Definition {

	private Map<String, List<String>> phrases;
	private Map<String, InstructionDefinition> instructions;
	private String matchRule;

	public Map<String, InstructionDefinition> getInstructions() {
		return instructions;
	}

	public void setInstructions(Map<String, InstructionDefinition> instructions) {
		this.instructions = instructions;
	}

	public Map<String, List<String>> getPhrases() {
		return phrases;
	}

	public void setPhrases(Map<String, List<String>> phrases) {
		this.phrases = phrases;
	}

	public String getMatchRule() {
		return matchRule;
	}

	public void setMatchRule(String matchRule) {
		this.matchRule = matchRule;
	}
}
