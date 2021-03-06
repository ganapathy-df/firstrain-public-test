/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:44:53 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertNull;

import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class ContentESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		Content content0 = new Content();
		content0.setTweets(null);
		assertNull(content0.getTotalItemCount());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		Content content0 = new Content();
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.SL;
		GraphNodeSet graphNodeSet0 = new GraphNodeSet(baseSet_SectionType0);
		content0.setTweetAccelerometer(graphNodeSet0);
		assertNull(content0.getItemOffset());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		Content content0 = new Content();
		content0.setTotalItemCount(null);
		assertNull(content0.getTotalItemCount());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		Content content0 = new Content();
		content0.setItemOffset(null);
		assertNull(content0.getItemOffset());
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		Content content0 = new Content();
		content0.setItemCount(null);
		assertNull(content0.getItemCount());
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		Content content0 = new Content();
		content0.setEvents(null);
		assertNull(content0.getItemCount());
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		Content content0 = new Content();
		content0.setEventBuckets(null);
		assertNull(content0.getTotalItemCount());
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		Content content0 = new Content();
		content0.setDocuments(null);
		assertNull(content0.getTotalItemCount());
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		Content content0 = new Content();
		content0.setDocumentBuckets(null);
		assertNull(content0.getItemOffset());
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		Content content0 = new Content();
		List<Tweet> list0 = content0.getTweets();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		Content content0 = new Content();
		GraphNodeSet graphNodeSet0 = content0.getTweetAccelerometer();
		assertNull(graphNodeSet0);
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		Content content0 = new Content();
		Integer integer0 = content0.getTotalItemCount();
		assertNull(integer0);
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		Content content0 = new Content();
		Integer integer0 = content0.getItemOffset();
		assertNull(integer0);
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		Content content0 = new Content();
		Integer integer0 = content0.getItemCount();
		assertNull(integer0);
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		Content content0 = new Content();
		List<Event> list0 = content0.getEvents();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		Content content0 = new Content();
		Map<String, List<Event>> map0 = content0.getEventBuckets();
		assertNull(map0);
	}

	@Test(timeout = 4000)
	public void test16() throws Exception {
		Content content0 = new Content();
		List<Document> list0 = content0.getDocuments();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test17() throws Exception {
		Content content0 = new Content();
		Map<String, List<Document>> map0 = content0.getDocumentBuckets();
		assertNull(map0);
	}
}
