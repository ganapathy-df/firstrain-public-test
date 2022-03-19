package com.firstrain.frapi.util;

import static org.junit.Assert.assertTrue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;

public class ContentTokeHandlerTest {

    @Test
    public void givenInputValueAndTokenWhenProcessContentThenReturnsExpectedResult() {
        // Arrange
        String inputValue = "Test Data-";
        String token = "-";

        // Act
        String result = ContentTokeHandler.processContent(inputValue, token);

        // Assert
        assertTrue("Test Data".equalsIgnoreCase(result));
    }

    @Test(expected = InvocationTargetException.class)
    public void givenReflectionSetupWhenNewInstanceRunsThenInvocationTargetExceptionIsThrown() throws Exception {
        // Arrange
        Class<ContentTokeHandler> entityHandlerClass = ContentTokeHandler.class;
        Constructor<?> constructor = entityHandlerClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // Act
        constructor.newInstance();
    }

}
