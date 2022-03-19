package com.firstrain.pojotest;

import com.firstrain.test.util.PojoTestUtil;

import com.firstrain.web.util.LoadConfiguration;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

import java.util.List;

public class PojoTest {

	private static final String DOMAIN_PKG_DIR = "com/firstrain/web/domain";
	private static final String EXCEPTION_PKG_DIR = "com/firstrain/web/exception";
	private static final String WEB_SERVICE_CORE_PKG_DIR = "com/firstrain/web/service/core";
	private static final String WEB_UTIL_PKG_DIR = "com/firstrain/web/util";

	@Test
	public void testOpenPojo() {
		Validator validator = ValidatorBuilder.create()
				.with(new SetterTester())
				.with(new GetterTester())
				.build();

		List<Class> classes = PojoTestUtil.getAllClasses(DOMAIN_PKG_DIR);
		classes.addAll(PojoTestUtil.getAllClasses(WEB_SERVICE_CORE_PKG_DIR));
		classes.addAll(PojoTestUtil.getAllClasses(EXCEPTION_PKG_DIR));
		classes.addAll(PojoTestUtil.getAllClasses(WEB_UTIL_PKG_DIR));

		removeNonPojoClasses(classes);
		for(Class clazz:classes) {
			validator.validate(PojoClassFactory.getPojoClass(clazz));
		}
	}

	private void removeNonPojoClasses(List<Class> classes) {
		classes.remove(LoadConfiguration.class);
	}
}