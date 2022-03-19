package com.firstrain.web.pojo;

import java.util.List;

public class InstructionDefinition {

	private String targetPhrase;
	private String qualifyingPhrase;
	private List<String> instruction;
	private List<String> location;

	public String getTargetPhrase() {
		return targetPhrase;
	}

	public void setTargetPhrase(String targetPhrase) {
		this.targetPhrase = targetPhrase;
	}

	public String getQualifyingPhrase() {
		return qualifyingPhrase;
	}

	public void setQualifyingPhrase(String qualifyingPhrase) {
		this.qualifyingPhrase = qualifyingPhrase;
	}

	public List<String> getInstruction() {
		return instruction;
	}

	public void setInstruction(List<String> instruction) {
		this.instruction = instruction;
	}

	public List<String> getLocation() {
		return location;
	}

	public void setLocation(List<String> location) {
		this.location = location;
	}
}
