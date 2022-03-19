/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:37:55 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import freemarker.template.Template;
import java.io.File;
import org.evosuite.runtime.mock.java.io.MockFile;
import org.evosuite.runtime.testdata.EvoSuiteFile;
import org.evosuite.runtime.testdata.FileSystemHandling;
import org.junit.Test;


public class FreemarkerTemplatesESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		MockFile mockFile0 = new MockFile("", "");
		freemarkerTemplates0.init(mockFile0);
		assertTrue(mockFile0.exists());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test1() throws Exception {
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		// Undeclared exception!
		freemarkerTemplates0.init();
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		MockFile mockFile0 = new MockFile("BkUq");
		freemarkerTemplates0.init(mockFile0);
		Template template0 = freemarkerTemplates0.getTemplate("BkUq");
		assertNull(template0);
	}

	@Test(timeout = 4000, expected = IllegalStateException.class)
	public void test4() throws Exception {
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		// Undeclared exception!
		freemarkerTemplates0.getTemplate("BkUq");
	}
}
