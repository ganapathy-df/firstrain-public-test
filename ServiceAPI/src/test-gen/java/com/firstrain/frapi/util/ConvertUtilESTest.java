/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:41:21 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Monitor;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SolrSearcher;
import java.util.List;
import org.junit.Test;


public class ConvertUtilESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test00() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		EventObj eventObj0 = new EventObj();
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_STOCK_PRICE;
		eventObj0.setEventGroup(iEvents_EventGroupEnum0);
		// Undeclared exception!
		convertUtil0.getTitle(eventObj0, "10-K", true);
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		EventObj eventObj0 = new EventObj();
		String string0 = convertUtil0.getTitle(eventObj0, null, false);
		assertNull(string0);
	}

	@Test(timeout = 4000, expected = NumberFormatException.class)
	public void test02() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		// Undeclared exception!
		convertUtil0.getIdFromAbsoluteId("title");
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		long long0 = convertUtil0.getIdFromAbsoluteId(null);
		assertEquals((-1L), long0);
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		User user0 = convertUtil0.getDomainUserFromDBUser(null);
		assertNull(user0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test05() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		Users users0 = new Users();
		BaseItem.FLAGS baseItem_FLAGS0 = BaseItem.FLAGS.ALL;
		users0.setFlags(baseItem_FLAGS0);
		UserGroupMap.MembershipType userGroupMap_MembershipType0 = UserGroupMap.MembershipType.ADMIN;
		convertUtil0.convertDbUserToDomainUser(users0, userGroupMap_MembershipType0);
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		Monitor monitor0 = convertUtil0.convertToTags(null);
		assertNull(monitor0);
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.DATE;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToServiceDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertSame(defaultEnums_DateBucketingMode1, defaultEnums_DateBucketingMode0);
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.SMART;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToServiceDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertEquals(DefaultEnums.DateBucketingMode.SMART, defaultEnums_DateBucketingMode1);
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.AUTO;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToServiceDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertSame(defaultEnums_DateBucketingMode0, defaultEnums_DateBucketingMode1);
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		List<Event> list0 = convertUtil0.convertToBaseTypeForGraph(null, false);
		assertTrue(list0.isEmpty());
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		List<Event> list0 = convertUtil0.convertToBaseType(null);
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		EventObj eventObj0 = new EventObj();
		eventObj0.setEventType(EventTypeEnum.TYPE_TRANSACTION);
		Event event0 = convertUtil0.convertToBaseType(eventObj0, false, false, false);
		assertTrue(event0.isHasExpired());
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		Tags.TagType tags_TagType0 = Tags.TagType.FOLDER_COMPTITOR;
		DefaultEnums.TagType defaultEnums_TagType0 = convertUtil0.convertToBaseTagType(tags_TagType0);
		assertEquals(6, defaultEnums_TagType0.getOrder());
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		Tags tags0 = new Tags();
		Monitor monitor0 = convertUtil0.convertToTags(tags0);
		assertNotNull(monitor0);
		assertFalse(monitor0.isMailActive());
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		BaseItem.OwnedByType baseItem_OwnedByType0 = BaseItem.OwnedByType.GROUP;
		DefaultEnums.OwnedByType defaultEnums_OwnedByType0 = convertUtil0
				.convertToBaseOwnedByType(baseItem_OwnedByType0);
		assertEquals(DefaultEnums.OwnedByType.GROUP, defaultEnums_OwnedByType0);
	}

	@Test(timeout = 4000)
	public void test16() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		UserGroupMap.MembershipType userGroupMap_MembershipType0 = UserGroupMap.MembershipType.ANONYMOUS;
		DefaultEnums.MembershipType defaultEnums_MembershipType0 = convertUtil0
				.convertToBaseMembershipType(userGroupMap_MembershipType0);
		assertEquals(DefaultEnums.MembershipType.ANONYMOUS, defaultEnums_MembershipType0);
	}

	@Test(timeout = 4000)
	public void test17() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.DATE;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToBaseDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertSame(defaultEnums_DateBucketingMode0, defaultEnums_DateBucketingMode1);
	}

	@Test(timeout = 4000)
	public void test18() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.AUTO;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToBaseDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertEquals(DefaultEnums.DateBucketingMode.AUTO, defaultEnums_DateBucketingMode1);
	}

	@Test(timeout = 4000)
	public void test19() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.SMART;
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode1 = convertUtil0
				.convertToBaseDateBucketingMode(defaultEnums_DateBucketingMode0);
		assertEquals(DefaultEnums.DateBucketingMode.SMART, defaultEnums_DateBucketingMode1);
	}

	@Test(timeout = 4000)
	public void test20() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		EntityEntry entityEntry0 = new EntityEntry("</span>");
		Entity entity0 = convertUtil0
				.convertEntityFromEntityEntry(entityEntry0, (short) (-103), (short) 40);
		assertEquals((short) 40, (short) entity0.getRelevanceScore());
		assertEquals((-103), (int) entity0.getBand());
		assertEquals((-768), (int) entity0.getType());
	}

	@Test(timeout = 4000)
	public void test21() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		Entity entity0 = convertUtil0
				.convertEntityFromEntityEntry(null, (short) (-6909), (short) (-6909));
		assertNull(entity0);
	}

	@Test(timeout = 4000)
	public void test22() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		EntityEntry entityEntry0 = new EntityEntry();
		Entity entity0 = convertUtil0.convertEntityFromEntityEntry(entityEntry0, (short) 2308);
		assertEquals(2308, (int) entity0.getBand());
		assertNotNull(entity0);
	}

	@Test(timeout = 4000)
	public void test23() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		ContentType contentType0 = ContentType.FILINGS_8K;
		Document document0 = convertUtil0
				.convertDocumentPOJOFromDocEntry(null, contentType0);
		assertEquals(ContentType.FILINGS_8K, document0.getContentType());
	}

	@Test(timeout = 4000)
	public void test24() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DocEntry docEntry0 = new DocEntry();
		DocEntry.DocImage docEntry_DocImage0 = new DocEntry.DocImage();
		docEntry0.docImage = docEntry_DocImage0;
		ContentType contentType0 = ContentType.WEBNEWS;
		Document document0 = convertUtil0.convertDocumentPOJOFromDocEntry(docEntry0, contentType0);
		assertEquals(0, document0.getScore());
		assertFalse(document0.isLoginRequired());
	}

	@Test(timeout = 4000)
	public void test25() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		DocEntry docEntry0 = new DocEntry();
		DocEntry.FavIcon docEntry_FavIcon0 = new DocEntry.FavIcon();
		docEntry0.favIcon = docEntry_FavIcon0;
		ContentType contentType0 = ContentType.FILINGS_10Q;
		Document document0 = convertUtil0.convertDocumentPOJOFromDocEntry(docEntry0, contentType0);
		assertEquals(ContentType.FILINGS_10Q, document0.getContentType());
		assertEquals(0, document0.getScore());
		assertFalse(document0.isLoginRequired());
	}

	@Test(timeout = 4000)
	public void test26() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		UserGroupMap.MembershipType userGroupMap_MembershipType0 = UserGroupMap.MembershipType.ADMIN;
		User user0 = convertUtil0.convertDbUserToDomainUser(null, userGroupMap_MembershipType0);
		assertNull(user0);
	}

	@Test(timeout = 4000)
	public void test27() throws Exception {
		ConvertUtil convertUtil0 = new ConvertUtil();
		convertUtil0.attachPropertiesForExpiredEvents(null, null);
	}
}