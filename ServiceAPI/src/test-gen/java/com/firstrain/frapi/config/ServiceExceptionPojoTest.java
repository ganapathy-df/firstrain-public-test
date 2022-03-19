package com.firstrain.frapi.config;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class ServiceExceptionPojoTest {

    @Test
    public void test_validate_ServiceException_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(ServiceException.class));
    }
}
