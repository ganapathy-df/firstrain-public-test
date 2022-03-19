package com.firstrain.frapi.obj;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class MgmtTurnoverServiceSpecPojoTest {

    @Test
    public void test_validate_MgmtTurnoverServiceSpec_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MgmtTurnoverServiceSpec.class));
    }

    @Test
    public void test_validate_MgmtTurnoverServiceSpec_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MgmtTurnoverServiceSpec.class));
    }

    @Test
    public void test_validate_MgmtTurnoverServiceSpec_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(MgmtTurnoverServiceSpec.class));
    }
}
