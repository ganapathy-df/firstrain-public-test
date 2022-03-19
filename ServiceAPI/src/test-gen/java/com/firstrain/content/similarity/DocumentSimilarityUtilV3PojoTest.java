package com.firstrain.content.similarity;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class DocumentSimilarityUtilV3PojoTest {

    @Test
    public void test_validate_DocumentSimilarityUtilV3_Setters() {
        Validator validator = TestChain.startWith(Testers.setterTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(DocumentSimilarityUtilV3.class));
    }

    @Test
    public void test_validate_DocumentSimilarityUtilV3_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(DocumentSimilarityUtilV3.class));
    }
}
