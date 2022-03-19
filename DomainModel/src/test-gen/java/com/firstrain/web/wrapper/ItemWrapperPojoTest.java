package com.firstrain.web.wrapper;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class ItemWrapperPojoTest {

    @Test
    public void test_validate_ItemWrapper_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(ItemWrapper.class));
    }

    @Test
    public void test_validate_ItemWrapper_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(ItemWrapper.class));
    }
}
