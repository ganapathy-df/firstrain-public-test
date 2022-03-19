package com.firstrain.web.exception;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class CustomExceptionSuccessPojoTest {

    @Test
    public void test_validate_CustomExceptionSuccess_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CustomExceptionSuccess.class));
    }

    @Test
    public void test_validate_CustomExceptionSuccess_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CustomExceptionSuccess.class));
    }
}
