package com.firstrain.frapi.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Before;
import org.junit.Test;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.service.DnbService;

public class EntityHandlerTest {

    private DnbService dnbService;
    private String token;
    private BlendDunsInput bdi;

    @Before
    public void setUp() {
        dnbService = mock(DnbService.class);
        token = "Sample Token";
        bdi = mock(BlendDunsInput.class);
    }

    @Test
    public void givenConditionWhenGenerateEntityRunsThenEntityIsNotNull() throws Exception {
        // Arrange
        when(dnbService.getDnbEntity(token, bdi)).thenReturn(new Entity());

        // Act
        Entity entity = EntityHandler.generateEntity(dnbService, token, bdi);

        // Assert
        assertNotNull(entity);
    }

    @Test
    public void givenConditionWhenGenerateEntityRunsThenEntityIsNull() throws Exception {
        // Arrange
        when(dnbService.getDnbEntity(token, bdi)).thenThrow(new Exception());

        // Act
        Entity entity = EntityHandler.generateEntity(dnbService, token, bdi);

        // Assert
        assertNull(entity);
    }

    @Test(expected = InvocationTargetException.class)
    public void givenReflectionSetupWhenNewInstanceRunsThenInvocationTargetExceptionIsThrown() throws Exception {
        // Arrange
        Class<EntityHandler> entityHandlerClass = EntityHandler.class;
        Constructor<?> constructor = entityHandlerClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // Act
        constructor.newInstance();
    }
}
