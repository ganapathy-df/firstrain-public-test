package com.firstrain.pojotest;

import com.firstrain.frapi.config.ServiceConfig;
import com.firstrain.frapi.events.EventConfigUtil;
import com.firstrain.frapi.obj.DocEntrySiteDocument;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.test.util.PojoTestUtil;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

import org.junit.Test;

import java.util.List;

public class PojoTest {

	private static final String FRAAPI_CONFIG_PKG_DIR = "com/firstrain/frapi/config";
	private static final String FRAPI_EVENTS_PKG_DIR = "com/firstrain/frapi/events";
	private static final String FRAPI_OBJ_PKG_DIR = "com/firstrain/frapi/obj";
	private static final String FRAPI_SERVICE_FILTERS_PKG_DIR = "com/firstrain/frapi/service/filters";
	private static final String FRAPI_UTIL_PKG_DIR = "com/firstrain/frapi/util";

	@Test
	public void testOpenPojo() {
		Validator validator = ValidatorBuilder.create()
				.with(new SetterTester())
				.with(new GetterTester())
				.build();

		List<Class> classes = PojoTestUtil.getAllClasses(FRAAPI_CONFIG_PKG_DIR);
		classes.addAll(PojoTestUtil.getAllClasses(FRAPI_EVENTS_PKG_DIR));
		classes.addAll(PojoTestUtil.getAllClasses(FRAPI_OBJ_PKG_DIR));
		classes.addAll(PojoTestUtil.getAllClasses(FRAPI_SERVICE_FILTERS_PKG_DIR));
		classes.addAll(PojoTestUtil.getAllClasses(FRAPI_UTIL_PKG_DIR));

		removeNonPojoClasses(classes);
		classes.add(EventConfigUtil.Filings8KConfObj.class);
		classes.add(EventConfigUtil.DelayedConfObj.class);
		classes.add(EventConfigUtil.WebVolumeConfObj.class);

		for(Class clazz:classes) {
			validator.validate(PojoClassFactory.getPojoClass(clazz));
		}
	}

	private void removeNonPojoClasses(List<Class> classes) {
		classes.remove(ServiceConfig.class);
		classes.remove(DocEntrySiteDocument.class);
		classes.remove(EventQueryCriteria.class);
		classes.remove(GraphQueryCriteria.class);
	}
}