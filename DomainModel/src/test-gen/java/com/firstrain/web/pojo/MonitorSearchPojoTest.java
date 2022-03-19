package com.firstrain.web.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class MonitorSearchPojoTest {

    @Test
    public void test_validate_MonitorSearch_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorSearch.class));
    }

    @Test
    public void test_validate_MonitorSearch_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorSearch.class));
    }
}
