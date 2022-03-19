package com.firstrain.frapi.service.filters;

import com.aurea.unittest.commons.pojo.Testers;
import com.aurea.unittest.commons.pojo.chain.TestChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import javax.annotation.Generated;
import org.junit.Test;

@Generated("GeneralPatterns")
public class SelectorsPojoTest {

    @Test
    public void test_validate_SelectorsWebVolumeSelector_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Selectors.WebVolumeSelector.class));
    }

    @Test
    public void test_validate_SelectorsStockPriceChangeSelector_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Selectors.StockPriceChangeSelector.class));
    }

    @Test
    public void test_validate_SelectorsTypeSelector_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Selectors.TypeSelector.class));
    }

    @Test
    public void test_validate_SelectorsGroupSelector_Constructors() {
        Validator validator = TestChain.startWith(Testers.constructorTester()).buildValidator();
        validator.validate(PojoClassFactory.getPojoClass(Selectors.GroupSelector.class));
    }
}
