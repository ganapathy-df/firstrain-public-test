package com.firstrain.frapi.pojo.wrapper;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import org.junit.Test;

public class TweetSetPojoTest {

    @Test
    public void test_validate_TweetSet_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(TweetSet.class));
    }

    @Test
    public void test_validate_TweetSet_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(TweetSet.class));
    }

    @Test
    public void test_validate_TweetSet_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(TweetSet.class));
    }

    @Test
    public void test_validate_TweetSetTweetGroup_Getters() {
        Validator validator = TestChain.startWith(Testers.getterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(TweetSet.TweetGroup.class));
    }

    @Test
    public void test_validate_TweetSetTweetGroup_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(TweetSet.TweetGroup.class));
    }
}
