package com.firstrain.dummy;

import org.junit.*;

public class DummyTest{
	@Test
	public void testAlwaysSuccess(){
		int i =42;
		Assert.assertEquals(42,i);
	}
}

