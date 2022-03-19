package com.firstrain.web.pojo;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class EntityMatchInputBeanPojoTest {

    @Test
    public void test_validate_EntityMatchInputBean_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EntityMatchInputBean.class));
    }

    @Test
    public void test_validate_EntityMatchInputBean_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EntityMatchInputBean.class));
    }

    @Test
    public void test_validate_EntityMatchInputBeanEntityInput_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EntityMatchInputBean.EntityInput.class));
    }

    @Test
    public void test_validate_EntityMatchInputBeanEntityInput_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EntityMatchInputBean.EntityInput.class));
    }
}
