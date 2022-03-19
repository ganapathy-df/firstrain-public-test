package com.firstrain.pojotest;

import com.firstrain.test.util.PojoTestUtil;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

import org.junit.Test;

import java.util.List;

public class PojoTest {
	private static final String WEB_SERVICE_CORE_PKG_DIR = "com/firstrain/web/service/core";

	@Test
	public void testOpenPojo() {
		Validator validator = ValidatorBuilder.create()
				.with(new SetterTester())
				.with(new GetterTester())
				.build();

		List<Class> classes = PojoTestUtil.getAllClasses(WEB_SERVICE_CORE_PKG_DIR);

		for(Class clazz:classes) {
			validator.validate(PojoClassFactory.getPojoClass(clazz));
		}
	}
}
