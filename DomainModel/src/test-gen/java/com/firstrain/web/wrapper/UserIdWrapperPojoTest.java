package com.firstrain.web.wrapper;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class UserIdWrapperPojoTest {

    @Test
    public void test_validate_UserIdWrapper_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(UserIdWrapper.class));
    }

    @Test
    public void test_validate_UserIdWrapper_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(UserIdWrapper.class));
    }
}
