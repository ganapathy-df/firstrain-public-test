package com.firstrain.frapi.pojo.wrapper;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class GraphNodeSetPojoTest {

    @Test
    public void test_validate_GraphNodeSet_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(GraphNodeSet.class));
    }

    @Test
    public void test_validate_GraphNodeSet_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(GraphNodeSet.class));
    }

    @Test
    public void test_validate_GraphNodeSet_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(GraphNodeSet.class));
    }
}
