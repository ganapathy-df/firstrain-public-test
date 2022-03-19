/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:12:07 GMT 2018
 */

package com.firstrain.frapi.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class NotableDetailsESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		notableDetails_NotableDetail0.setUserImage(" 4");
		assertNull(notableDetails_NotableDetail0.getScreenName());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		notableDetails_NotableDetail0.setScreenName(null);
		assertNull(notableDetails_NotableDetail0.getScreenName());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		notableDetails_NotableDetail0.setProfileUrl(null);
		assertNull(notableDetails_NotableDetail0.getScreenName());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		String string0 = notableDetails_NotableDetail0.getUserImage();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		String string0 = notableDetails_NotableDetail0.getScreenName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		NotableDetails.NotableDetail notableDetails_NotableDetail0 = new NotableDetails.NotableDetail();
		String string0 = notableDetails_NotableDetail0.getProfileUrl();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		NotableDetails notableDetails0 = new NotableDetails();
		notableDetails0.setTweetId("com.firstrain.frapi.pojo.NotableDetails");
		assertEquals("com.firstrain.frapi.pojo.NotableDetails", notableDetails0.getTweetId());
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		NotableDetails notableDetails0 = new NotableDetails();
		notableDetails0.setNotableDetails(null);
		assertNull(notableDetails0.getTweetId());
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		NotableDetails notableDetails0 = new NotableDetails();
		String string0 = notableDetails0.getTweetId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test9() throws Exception {
		NotableDetails notableDetails0 = new NotableDetails();
		List<NotableDetails.NotableDetail> list0 = notableDetails0.getNotableDetails();
		assertNull(list0);
	}
}
