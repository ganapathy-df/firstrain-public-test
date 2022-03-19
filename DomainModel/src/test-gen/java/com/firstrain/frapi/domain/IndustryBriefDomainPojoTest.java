package com.firstrain.frapi.domain;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class IndustryBriefDomainPojoTest {

    @Test
    public void test_validate_IndustryBriefDomain_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(IndustryBriefDomain.class));
    }

    @Test
    public void test_validate_IndustryBriefDomain_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(IndustryBriefDomain.class));
    }
}
