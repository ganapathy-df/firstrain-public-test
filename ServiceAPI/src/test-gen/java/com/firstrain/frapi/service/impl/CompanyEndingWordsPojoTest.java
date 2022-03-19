package com.firstrain.frapi.service.impl;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class CompanyEndingWordsPojoTest {

    @Test
    public void test_validate_CompanyEndingWords_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CompanyEndingWords.class));
    }
}
