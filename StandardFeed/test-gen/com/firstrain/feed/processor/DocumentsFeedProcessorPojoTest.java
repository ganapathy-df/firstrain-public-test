package com.firstrain.feed.processor;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class DocumentsFeedProcessorPojoTest {

    @Test
    public void test_validate_DocumentsFeedProcessor_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(DocumentsFeedProcessor.class));
    }
}
