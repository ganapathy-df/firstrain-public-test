package com.firstrain.frapi.domain;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class VisualizationDataPojoTest {

    @Test
    public void test_validate_VisualizationData_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(VisualizationData.class));
    }

    @Test
    public void test_validate_VisualizationData_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(VisualizationData.class));
    }

    @Test
    public void test_validate_VisualizationDataGraph_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(VisualizationData.Graph.class));
    }

    @Test
    public void test_validate_VisualizationDataNodeBucket_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(VisualizationData.NodeBucket.class));
    }

    @Test
    public void test_validate_VisualizationDataNode_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(VisualizationData.Node.class));
    }
}
