package com.firstrain.frapi.events;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class EventConfigUtilPojoTest {

    @Test
    public void test_validate_EventConfigUtilFilings8KConfObj_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.Filings8KConfObj.class));
    }

    @Test
    public void test_validate_EventConfigUtilFilings8KConfObj_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.Filings8KConfObj.class));
    }

    @Test
    public void test_validate_EventConfigUtilDelayedConfObj_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.DelayedConfObj.class));
    }

    @Test
    public void test_validate_EventConfigUtilDelayedConfObj_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.DelayedConfObj.class));
    }

    @Test
    public void test_validate_EventConfigUtilWebVolumeConfObj_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.WebVolumeConfObj.class));
    }

    @Test
    public void test_validate_EventConfigUtilWebVolumeConfObj_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(EventConfigUtil.WebVolumeConfObj.class));
    }
}
