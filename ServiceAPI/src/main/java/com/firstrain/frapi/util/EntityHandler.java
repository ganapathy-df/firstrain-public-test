package com.firstrain.frapi.util;

import org.apache.log4j.Logger;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;

public final class EntityHandler {

    private static final Logger logger = Logger.getLogger(EntityHandler.class);

    private EntityHandler() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Entity generateEntity(DnbService dnbService, String token, BlendDunsInput bdi) {
        Entity entity = null;
        try {
            entity = dnbService.getDnbEntity(token, bdi);
        } catch (Exception e) {
            logger.error("Error while getting DnbEntity:" + token, e);
        }
        return entity;
    }
    
    public static Event addId(Event event) {
		String eventId = event.getEventId();
		EventInformationEnum eventGroup = event.getEventInformationEnum();

		if (eventGroup == EventInformationEnum.MT_DEPARTURE 
				|| eventGroup == EventInformationEnum.MT_HIRE
				|| eventGroup == EventInformationEnum.MT_MOVE) {
			eventId = FRAPIConstant.MT_PREFIX + eventId;
		} else if (eventGroup == EventInformationEnum.PRICE_UP 
				|| eventGroup == EventInformationEnum.PRICE_DOWN) {
			eventId = FRAPIConstant.EVENTS_PREFIX + eventId;
		} else if (eventGroup == EventInformationEnum.WEB_VOLUME) {
			eventId = FRAPIConstant.EVENTS_PREFIX + eventId;
		} else if (eventGroup == EventInformationEnum.SEC) {
			if (event.getSecFormType() != null) {
				eventId = FRAPIConstant.SEC_FILING_PREFIX + eventId;
			} else {
				eventId = FRAPIConstant.SEC_PREFIX + eventId;
			}
		}

		if (eventId != null) {
			event.setEventId(eventId);
		}
		return event;
	}
}
