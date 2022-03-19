/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:38:23 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.evosuite.shaded.org.mockito.Mockito.doReturn;
import static org.evosuite.shaded.org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.firstrain.content.similarity.CandidateIndex;
import com.firstrain.content.similarity.CandidateStore;
import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.db.obj.GroupDomainMap;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityInfoCache;
import com.firstrain.solr.client.EntityInfoCacheLucene;
import com.firstrain.solr.client.QuoteEntry;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServer;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;


public class ServicesAPIUtilESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test00() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		ServicesAPIUtil.PojoDocumentDateComparator servicesAPIUtil_PojoDocumentDateComparator0 = servicesAPIUtil0.new PojoDocumentDateComparator();
		ContentType contentType0 = ContentType.FILINGS_SEC345;
		Document document0 = convertUtil0
				.convertDocumentPOJOFromDocEntry(null, contentType0);
		// Undeclared exception!
		servicesAPIUtil_PojoDocumentDateComparator0.compare(document0, document0);
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		BaseSpec baseSpec0 = new BaseSpec();
		EnterprisePref enterprisePref0 = new EnterprisePref();
		enterprisePref0.setPublicSourceIdsSSV("N1_9$");
		BaseSpec baseSpec1 = servicesAPIUtil0.setSourceContent(true, true, baseSpec0, enterprisePref0);
		assertNull(baseSpec1.getNeedMatchedEntities());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		BaseSpec baseSpec0 = new BaseSpec();
		EnterprisePref enterprisePref0 = new EnterprisePref();
		BaseSpec baseSpec1 = servicesAPIUtil0.setSourceContent(false, true, baseSpec0, enterprisePref0);
		assertFalse(baseSpec1.isCustomized());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.removeKeywordsFromQ("E:a?Uw", "E:a?Uw");
		assertEquals("", string0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test04() throws Exception {
		MonitorWizardFilters monitorWizardFilters0 = new MonitorWizardFilters();
		MonitorWizardFilters.Advanced monitorWizardFilters_Advanced0 = new MonitorWizardFilters.Advanced(
				"~-P]j.K8m", null);
		monitorWizardFilters0.advanced = monitorWizardFilters_Advanced0;
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		servicesAPIUtil0.removeFilters(null, monitorWizardFilters0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test05() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		// Undeclared exception!
		servicesAPIUtil0.removeFilterFromKeywords("", (List<String>) null);
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0
				.removeFilterFromKeywords("DP-!$l+p2_/${RjDXt", "DP-!$l+p2_/${RjDXt");
		assertEquals("", string0);
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		Map<String, List<String>> map0 = servicesAPIUtil0.prepareQueryMap("G`d1sju*p!u;]NTF5w");
		assertEquals(0, map0.size());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test08() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		MonitorWizardFilters monitorWizardFilters0 = new MonitorWizardFilters();
		// Undeclared exception!
		servicesAPIUtil0
				.prepareQForAddOrRemoveFilter("enter HttpState.addCookies(Cookie[])", monitorWizardFilters0,
						monitorWizardFilters0);
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.prepareFq(null);
		assertEquals("", string0);
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.CM;
		Graph graph0 = new Graph(null, null, baseSet_SectionType0);
		servicesAPIUtil0.mapHistoricalStatsToTriggers(graph0, null);
		assertEquals(10, graph0.getCharttopmargin());
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		QuoteEntry.QuoteType quoteEntry_QuoteType0 = QuoteEntry.QuoteType.TYPE_OTR;
		QuoteEntry quoteEntry0 = new QuoteEntry("6qF zTtv/i)Lc7mn*9M", "3N8~blX(5kn?g''a4'S", (short) 0,
				quoteEntry_QuoteType0);
		servicesAPIUtil0.highlightQuote(quoteEntry0);
		assertEquals("6qF zTtv/i)Lc7mn*9M", quoteEntry0.getText());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test12() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		doReturn(null).when(entityBaseServiceRepository0).getEntityInfoCache();
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		// Undeclared exception!
		servicesAPIUtil0.getSectorCatIdFromEntitySearchToken("l_f3yW|\"k#");
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityInfoCache.CacheSearchSpec entityInfoCache_CacheSearchSpec0 = new EntityInfoCache.CacheSearchSpec();
		EntityInfoCacheLucene entityInfoCacheLucene0 = new EntityInfoCacheLucene(null, 2,
				false, entityInfoCache_CacheSearchSpec0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		doReturn(entityInfoCacheLucene0).when(entityBaseServiceRepository0).getEntityInfoCache();
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.getSearchTokenOfCatId("/26");
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		doReturn(null).when(entityBaseServiceRepository0).getEntityInfoCache();
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.getSearchTokenOfCatId("/26");
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.getQToken("org.apache.lucene.analysis.CharTokenizer",
				"org.apache.lucene.analysis.CharTokenizer");
		assertEquals("org.apache.lucene.analysis.CharTokenizer", string0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test16() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityInfoCache.CacheSearchSpec entityInfoCache_CacheSearchSpec0 = new EntityInfoCache.CacheSearchSpec(
				"or");
		EntityInfoCacheLucene entityInfoCacheLucene0 = new EntityInfoCacheLucene(null,
				entityInfoCache_CacheSearchSpec0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		doReturn(entityInfoCacheLucene0).when(entityBaseServiceRepository0).getEntityInfoCache();
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		// Undeclared exception!
		servicesAPIUtil0.getNDaysCountForEntities(null, 9393);
	}

	@Test(timeout = 4000)
	public void test17() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		doReturn(null).when(entityBaseServiceRepository0).getEntityInfoCache();
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		Map<Long, Integer> map0 = servicesAPIUtil0.getNDaysCountForEntities(null, 9393);
		assertNull(map0);
	}

	@Test(timeout = 4000)
	public void test18() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		MgmtTurnoverServiceSpec mgmtTurnoverServiceSpec0 = servicesAPIUtil0
				.getMgmtSpec(3022, 2929, (-30));
		assertEquals(2929, mgmtTurnoverServiceSpec0.days);
		assertFalse(mgmtTurnoverServiceSpec0.quarterlyCount);
		assertFalse(mgmtTurnoverServiceSpec0.monthlyCount);
		assertEquals(3022, mgmtTurnoverServiceSpec0.getRows());
		assertTrue(mgmtTurnoverServiceSpec0.details);
		assertEquals((-30), mgmtTurnoverServiceSpec0.getStart());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test19() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		servicesAPIUtil0.getKeywordsAndFilters(null);
	}

	@Test(timeout = 4000)
	public void test20() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.getKeywordFromFilterString(null, 1764L);
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test21() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		String string0 = servicesAPIUtil0.getKeywordFromFilterString("T<Aub7oJQ/$?|*?qgz", (-989L));
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test22() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		boolean boolean0 = servicesAPIUtil0.getHasMoreValue(1327, 2, null);
		assertTrue(boolean0);
	}

	@Test(timeout = 4000)
	public void test23() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		Short short0 = (short) 7743;
		boolean boolean0 = servicesAPIUtil0.getHasMoreValue(9393, 9393, short0);
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test24() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		DefaultEnums.DateBucketingMode defaultEnums_DateBucketingMode0 = DefaultEnums.DateBucketingMode.DATE;
		DateBucketUtils.BucketSpec dateBucketUtils_BucketSpec0 = servicesAPIUtil0
				.getDateBucketingSpec(defaultEnums_DateBucketingMode0, "}QC");
		assertEquals(1, dateBucketUtils_BucketSpec0.bucketSizeThreshold);
		assertTrue(dateBucketUtils_BucketSpec0.dateBucket);
	}

	@Test(timeout = 4000, expected = IllegalArgumentException.class)
	public void test25() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		// Undeclared exception!
		servicesAPIUtil0.generateRandomString((-2828));
	}

	@Test(timeout = 4000)
	public void test26() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		CandidateIndex candidateIndex0 = new CandidateIndex(0);
		CandidateStore candidateStore0 = new CandidateStore();
		DocumentSimilarityUtil documentSimilarityUtil0 = new DocumentSimilarityUtil(null,
				"9fAo*HLCQu[(}*+D8", candidateIndex0, candidateStore0);
		List<DocEntry> list0 = servicesAPIUtil0
				.filterSimilarEntries(null, documentSimilarityUtil0, (-1773), false);
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test27() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		long long0 = servicesAPIUtil0.checkValidDomain(null, null);
		assertEquals((-1L), long0);
	}

	@Test(timeout = 4000)
	public void test28() throws Exception {
		ServicesAPIUtil servicesAPIUtil0 = new ServicesAPIUtil();
		ConvertUtil convertUtil0 = new ConvertUtil();
		Injector.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "convertUtil",
				convertUtil0);
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector
				.inject(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class, "entityBaseServiceRepository",
						entityBaseServiceRepository0);
		Injector.validateBean(servicesAPIUtil0, com.firstrain.frapi.util.ServicesAPIUtil.class);
		MonitorWizardFilters monitorWizardFilters0 = new MonitorWizardFilters();
		servicesAPIUtil0.addFilters(null, monitorWizardFilters0, null);
	}
}
