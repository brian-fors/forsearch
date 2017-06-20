package com.fors.ir.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fors.ir.controller.Main;

public class ElasticIndex {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String INDEXNAME = "patdemo";
	TransportClient client;
	IndexRequestBuilder builder;
	
	public void create() {
		XContentBuilder mapping = null;
		XContentBuilder mappingJsonBuilder = null;
		XContentBuilder settingsJsonBuilder = null;
		
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY)
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

			if (indexExists() && Main.REBUILD_INDEX) {
				deleteIndex();
			}
			builder = client.prepareIndex(INDEXNAME, "document");
			if (Main.REBUILD_INDEX) {
				CreateIndexResponse indexCreateResponse =  client.admin()
					      .indices()
					      .create(new CreateIndexRequest(INDEXNAME))
					      .actionGet();
			}

		} catch (UnknownHostException e) {
			LOGGER.error("Couldn't create Index=[patdemo]",e);
			e.printStackTrace();
		}
	}
	
	public boolean indexExists(){
		IndicesExistsResponse indexExistsResponse =  client.admin()
															    .indices()
															    .exists(new IndicesExistsRequest(INDEXNAME))
															    .actionGet(); 
		return indexExistsResponse.isExists(); 
	}
	
	public boolean deleteIndex(){
		DeleteIndexResponse indexDeleteResponse =  client.admin()
														      .indices()
														      .delete(new DeleteIndexRequest(INDEXNAME))
														      .actionGet();
		if (indexDeleteResponse.isAcknowledged()){
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Index=[" + INDEXNAME + "] has been deleted");
			}			
		}
		else{
			LOGGER.error("Couldn't delete Index=[" + INDEXNAME + "]");
		}		
		return indexDeleteResponse.isAcknowledged(); 
	}	
	
	public void indexDocuments(HashMap<Integer, Document> docs) {
		try {
			for (Document doc : docs.values()) {
				IndexResponse response;
				String docId = doc.getDocId();
				builder.setId(docId);
				builder.setSource(doc.toJson());
				response = builder.execute().actionGet();
			}
		} catch (Exception e) {
			LOGGER.error("Couldn't index documents",e);
			e.printStackTrace();
		}
	}
	
	public void bulkIndexDocuments(HashMap<Integer, Document> docs) {
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int bulkLimit = 25600;
			int i = 0;
			
			// disable index refresh during bulk update
			if (indexExists()) {
				client.admin().indices().prepareUpdateSettings(INDEXNAME)   
		        	.setSettings(Settings.builder()                     
		            .put("index.refresh_interval", "300s")
		        )
		        .get();
			}
			
			for (Document doc : docs.values()) {
				i++;
				String docId = doc.getDocId();
				bulkRequest.add(client.prepareIndex(INDEXNAME, "document", docId)
				        .setSource(doc.toJson())
				        .setId(docId)
				        );		
				if (i >= bulkLimit) {
					BulkResponse bulkResponse = bulkRequest.get();
					if (bulkResponse.hasFailures()) {
						LOGGER.error("Bulk index failures: ", bulkResponse.buildFailureMessage());
					}
					i=0;
				}
			}
			BulkResponse bulkResponse = bulkRequest.get();
			if (bulkResponse.hasFailures()) {
				LOGGER.error("Bulk index failures: ", bulkResponse.buildFailureMessage());
			}
			
			// re-enable index refresh to 1s, after bulk update
			if (indexExists()) {
				client.admin().indices().prepareUpdateSettings(INDEXNAME)   
		        	.setSettings(Settings.builder()                     
		            .put("index.refresh_interval", "1s")
		        )
		        .get();			
			}
			
		} catch (Exception e) {
			LOGGER.error("Couldn't bulk index documents",e);
			e.printStackTrace();
		}
		
	}
	
	 
	public SearchResponse search(Document doc) {
		SearchResponse response = client.prepareSearch(INDEXNAME)
				.setQuery(QueryBuilders.queryStringQuery(doc.toString()))
				.setMinScore(Main.maxScoreThreshold)
				.setSize(10)
				.get();
		return response;
	}

}
