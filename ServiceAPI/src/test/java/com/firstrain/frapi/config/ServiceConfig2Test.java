package com.firstrain.frapi.config;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import com.firstrain.solr.client.DistributedSearchConfigBuilder;
import com.firstrain.solr.client.DistributedSolrSearcher;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceConfig.class, DistributedSearchConfigBuilder.class})
public class ServiceConfig2Test {

    private static final String TEST_CONFIG_PATH = "src/test/resources/ServiceConfig.xml";

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Mock
    private SolrServer solrServer;

    private DistributedSolrSearcher.DistributedSearchConfig config;

    @Before
    public void setUp() {
        mockStatic(DistributedSearchConfigBuilder.class);
        SolrServer[] solrServers = new SolrServer[1];
        solrServers[0] = solrServer;
        config = new DistributedSolrSearcher.DistributedSearchConfig(solrServers, null);
    }

    @Test
    public void getInstanceWithOneRetry() throws Exception {
        // Arrange
        when(DistributedSearchConfigBuilder.createFrom(any(SolrServer.class), anyString()))
                .thenReturn(null).thenReturn(config);

        // Act
        ServiceConfig serviceConfig = ServiceConfig.getInstance(TEST_CONFIG_PATH);

        // Verify
        collector.checkThat(serviceConfig.getRetryDelaySeconds(), equalTo(1));
        collector.checkThat(serviceConfig.getSolrVsPingQ().size(), equalTo(8));

    }
}
