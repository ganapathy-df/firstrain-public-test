package com.firstrain.frapi.domain;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class BlendDunsInputPojoTest {

    @Test
    public void test_validate_BlendDunsInput_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(BlendDunsInput.class));
    }

    @Test
    public void test_validate_BlendDunsInput_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(BlendDunsInput.class));
    }
}
