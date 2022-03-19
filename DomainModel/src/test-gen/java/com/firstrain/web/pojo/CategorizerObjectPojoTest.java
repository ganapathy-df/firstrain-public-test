package com.firstrain.web.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class CategorizerObjectPojoTest {

    @Test
    public void test_validate_CategorizerObject_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.class));
    }

    @Test
    public void test_validate_CategorizerObject_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.class));
    }

    @Test
    public void test_validate_CategorizerObject_ToString() {
        Validator validator = TestChain.startWith(Testers.toStringTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.class));
    }

    @Test
    public void test_validate_CategorizerObject_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.class));
    }

    @Test
    public void test_validate_CategorizerObjectCatEntityWrapper_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.CatEntityWrapper.class));
    }

    @Test
    public void test_validate_CategorizerObjectCatEntityWrapper_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.CatEntityWrapper.class));
    }

    @Test
    public void test_validate_CategorizerObjectCatEntity_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.CatEntity.class));
    }

    @Test
    public void test_validate_CategorizerObjectCatEntity_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.CatEntity.class));
    }

    @Test
    public void test_validate_CategorizerObjectAttribute_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.Attribute.class));
    }

    @Test
    public void test_validate_CategorizerObjectAttribute_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(CategorizerObject.Attribute.class));
    }
}
