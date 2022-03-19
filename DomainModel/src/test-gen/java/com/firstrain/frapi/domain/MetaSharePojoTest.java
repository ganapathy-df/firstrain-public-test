package com.firstrain.frapi.domain;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class MetaSharePojoTest {

    @Test
    public void test_validate_MetaShare_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.class));
    }

    @Test
    public void test_validate_MetaShare_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.class));
    }

    @Test
    public void test_validate_MetaShareGroupShare_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.GroupShare.class));
    }

    @Test
    public void test_validate_MetaShareGroupShare_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.GroupShare.class));
    }

    @Test
    public void test_validate_MetaShareUserOwner_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.UserOwner.class));
    }

    @Test
    public void test_validate_MetaShareUserOwner_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MetaShare.UserOwner.class));
    }
}
