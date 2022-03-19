package com.firstrain.web.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class CategorizerEntityObjectPojoTest {

    @Test
    public void test_validate_CategorizerEntityObject_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.class));
    }

    @Test
    public void test_validate_CategorizerEntityObject_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.class));
    }

    @Test
    public void test_validate_CategorizerEntityObject_ToString() {
        Validator validator = TestChain.startWith(Testers.toStringTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.class));
    }

    @Test
    public void test_validate_CategorizerEntityObject_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.class));
    }

    @Test
    public void test_validate_CategorizerEntityObjectCatEntityObjectWrapper_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.CatEntityObjectWrapper.class));
    }

    @Test
    public void test_validate_CategorizerEntityObjectCatEntityObjectWrapper_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerEntityObject.CatEntityObjectWrapper.class));
    }
}
