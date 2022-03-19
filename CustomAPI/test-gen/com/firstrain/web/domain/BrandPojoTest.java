package com.firstrain.web.domain;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class BrandPojoTest {

    @Test
    public void test_validate_Brand_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Brand.class));
    }

    @Test
    public void test_validate_Brand_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Brand.class));
    }
}
