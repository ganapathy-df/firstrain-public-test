package com.firstrain.frapi.service;

import com.firstrain.frapi.events.IEvents;

/**
 * @author Akanksha
 * 
 */

public interface EventSelector {
	public boolean isSelected(IEvents e);
}

