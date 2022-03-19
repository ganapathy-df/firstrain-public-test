/**
 * � Copyright 2012, FirstRain, Inc. All Rights Reserved The contents of this file are the property of FirstRain, Inc. and are subject To a
 * License agreement with FirstRain; you may not use this file except in Compliance with that License. You may obtain a copy of that license
 * from: Legal Department FirstRain, Inc. 1510 Fashion Island Blvd. Suite 120 San Mateo, CA 94404
 * 
 * This software is distributed under the License and is provided on an as is Basis, without warranty of any kind, either express or
 * implied, unless Otherwise provided in the License. See the License for governing rights and Limitations under the License.
 *
 * @author "vgupta"
 *
 */

package com.firstrain.frapi.domain;

public class MgmtChart {
	private String labelAsString;
	private String valuesAsString;
	private int max;

	public String getLabelAsString() {
		return labelAsString;
	}

	public void setLabelAsString(String labelAsString) {
		this.labelAsString = labelAsString;
	}

	public String getValuesAsString() {
		return valuesAsString;
	}

	public void setValuesAsString(String valuesAsString) {
		this.valuesAsString = valuesAsString;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}
