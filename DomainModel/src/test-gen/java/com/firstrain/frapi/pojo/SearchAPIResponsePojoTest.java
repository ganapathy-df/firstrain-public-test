package com.firstrain.frapi.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class SearchAPIResponsePojoTest {

    @Test
    public void test_validate_SearchAPIResponse_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(SearchAPIResponse.class));
    }

    @Test
    public void test_validate_SearchAPIResponse_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(SearchAPIResponse.class));
    }
}
