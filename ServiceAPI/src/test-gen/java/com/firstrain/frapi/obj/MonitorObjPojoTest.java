package com.firstrain.frapi.obj;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class MonitorObjPojoTest {

    @Test
    public void test_validate_MonitorObj_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.class));
    }

    @Test
    public void test_validate_MonitorObj_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.class));
    }

    @Test
    public void test_validate_MonitorObjSearchResponse_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.SearchResponse.class));
    }

    @Test
    public void test_validate_MonitorObjTopicNBL_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.TopicNBL.class));
    }

    @Test
    public void test_validate_MonitorObjTopicNBL_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.TopicNBL.class));
    }

    @Test
    public void test_validate_MonitorObjRelatedInfo_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.RelatedInfo.class));
    }

    @Test
    public void test_validate_MonitorObjRelatedInfo_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.RelatedInfo.class));
    }

    @Test
    public void test_validate_MonitorObjRelatedInfo_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MonitorObj.RelatedInfo.class));
    }
}
