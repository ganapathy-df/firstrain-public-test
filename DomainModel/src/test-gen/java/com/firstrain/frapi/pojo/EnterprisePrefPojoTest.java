package com.firstrain.frapi.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class EnterprisePrefPojoTest {

    @Test
    public void test_validate_EnterprisePref_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EnterprisePref.class));
    }

    @Test
    public void test_validate_EnterprisePref_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EnterprisePref.class));
    }
}
