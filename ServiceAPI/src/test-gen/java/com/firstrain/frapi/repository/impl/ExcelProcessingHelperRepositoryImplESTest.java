/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:47:28 GMT 2018
 */

package com.firstrain.frapi.repository.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileDescriptor;
import java.util.Map;
import java.util.Set;
import org.evosuite.runtime.javaee.injection.Injector;
import org.evosuite.runtime.mock.java.io.MockFileInputStream;
import org.junit.Test;


public class ExcelProcessingHelperRepositoryImplESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		ExcelProcessingHelperRepositoryImpl excelProcessingHelperRepositoryImpl0 = new ExcelProcessingHelperRepositoryImpl();
		Injector.executePostConstruct(excelProcessingHelperRepositoryImpl0,
				com.firstrain.frapi.repository.impl.ExcelProcessingHelperRepositoryImpl.class);
		FileDescriptor fileDescriptor0 = new FileDescriptor();
		MockFileInputStream mockFileInputStream0 = new MockFileInputStream(fileDescriptor0);
		excelProcessingHelperRepositoryImpl0.loadCompanyEnding(mockFileInputStream0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		ExcelProcessingHelperRepositoryImpl excelProcessingHelperRepositoryImpl0 = new ExcelProcessingHelperRepositoryImpl();
		Injector.executePostConstruct(excelProcessingHelperRepositoryImpl0,
				com.firstrain.frapi.repository.impl.ExcelProcessingHelperRepositoryImpl.class);
		Map<Integer, Integer> map0 = excelProcessingHelperRepositoryImpl0.getPaltinumSourceVsRank();
		assertTrue(map0.isEmpty());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		ExcelProcessingHelperRepositoryImpl excelProcessingHelperRepositoryImpl0 = new ExcelProcessingHelperRepositoryImpl();
		Injector.executePostConstruct(excelProcessingHelperRepositoryImpl0,
				com.firstrain.frapi.repository.impl.ExcelProcessingHelperRepositoryImpl.class);
		Set<String> set0 = excelProcessingHelperRepositoryImpl0.getCompanyEndingWordsRegex();
		assertNull(set0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		ExcelProcessingHelperRepositoryImpl excelProcessingHelperRepositoryImpl0 = new ExcelProcessingHelperRepositoryImpl();
		Injector.executePostConstruct(excelProcessingHelperRepositoryImpl0,
				com.firstrain.frapi.repository.impl.ExcelProcessingHelperRepositoryImpl.class);
		Set<String> set0 = excelProcessingHelperRepositoryImpl0.getCompanyEndingWords();
		assertNull(set0);
	}
}
