package com.firstrain.frapi.util;

import static org.hamcrest.CoreMatchers.equalTo;
import java.util.Date;
import org.junit.Test;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.pojo.Event;

import org.junit.Rule;
import org.junit.rules.ErrorCollector;

public class ConvertUtilTest {

	@Rule
	public final ErrorCollector collector = new ErrorCollector();

	@Test
	public void givenProperValuesWhenConvertToBaseTypeIsCalledThenReturnProperValues() {
		// Arrange
		ConvertUtil utils = new ConvertUtil();
		boolean ipad = false;
		boolean isSingleSearch = false;
		boolean isDetail = false;
		EventObj eventObj = new EventObj();
		eventObj.setCaption("Test Caption");
		eventObj.setDate(new Date());
		eventObj.setDescription("Test Description");
		eventObj.setEventExpired(true);
		eventObj.setDayId(1);
		eventObj.setEventType(EventTypeEnum.TYPE_DELAYED_SEC_FILING);

		// Act
		Event retEvent = utils.convertToBaseType(eventObj, ipad, isSingleSearch, isDetail);

		// Assert
		collector.checkThat(eventObj.getCaption(), equalTo(retEvent.getCaption()));
		collector.checkThat(eventObj.getDate(), equalTo(retEvent.getDate()));
		collector.checkThat(eventObj.getDescription(), equalTo(retEvent.getDescription()));
		collector.checkThat(eventObj.hasExpired(), equalTo(retEvent.isHasExpired()));
		collector.checkThat(eventObj.getDayId(), equalTo(retEvent.getDayId()));

	}

}
