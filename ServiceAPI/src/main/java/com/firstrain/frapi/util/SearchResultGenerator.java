package com.firstrain.frapi.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SearchResult;

public final class SearchResultGenerator {

    private static final Logger logger = Logger.getLogger(SearchResultGenerator.class);

    private SearchResultGenerator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static SearchResult collectSearchResults(Future<SearchResult> analystCommentsFuture60Days,
            Future<SearchResult> analystCommentsFuture180Days, BaseSpec baseSpec)
            throws InterruptedException, ExecutionException {
        SearchResult searchResults = null;
        if (analystCommentsFuture60Days != null) {
            searchResults = analystCommentsFuture60Days.get();
            List<DocEntry> entries = searchResults.buckets.get(0).docs;
            if (entries == null || entries.size() < baseSpec.getCount()) {
                logger.debug("60 days results are not sufficient, so going to 180 days");
                if (entries != null) {
                    logger.debug("60 days results count: " + entries.size());
                }
                if (analystCommentsFuture180Days != null) {
                    searchResults = analystCommentsFuture180Days.get();
                }
            }
        } else if (analystCommentsFuture180Days != null) {
            searchResults = analystCommentsFuture180Days.get();
        }
        return searchResults;
    }
}
