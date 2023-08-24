package com.codestar.HAMI.elasticsearch.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.elasticsearch.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

//@Repository
public class ChatElasticRepository{

//    @Autowired
//    ElasticsearchClient elasticsearchClient;
//
//    private final String indexName = "chat";
//
//    public void createOrUpdate(ChatElasticModel chatElasticModel) throws IOException {
//        IndexResponse response = elasticsearchClient.index(i -> i
//                .index(indexName)
//                .id(chatElasticModel.getId().toString())
//                .document(chatElasticModel)
//        );
//        if (response.result().name().equals("Created")) {
//            System.out.println("Document has been successfully created.");
//        } else if (response.result().name().equals("Updated")) {
//            System.out.println("Document has been successfully updated.");
//        }
//        else {
//            System.out.println("Error in create Or updating document message");
//        }
//    }
//
//    public void deleteById(Long chatId) throws IOException {
//        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(chatId.toString()));
//
//        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
//        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
//            System.out.println("Chat with id " + deleteResponse.id() + " has been deleted.");
//        }
//        System.out.println("Chat not found");
//        System.out.println("Chat with id " + deleteResponse.id() + " does not exist.");
//    }
//
//    public List<Hit<ChatElasticModel>> searchWithFuzziness(String fieldValue)throws IOException{
//        Supplier<Query> supplier  = ElasticSearchUtil.supplierWithNameField(fieldValue);
//        SearchResponse<ChatElasticModel> searchResponse = elasticsearchClient.search(s->s.index(indexName).query(supplier.get()),ChatElasticModel.class);
//        return searchResponse.hits().hits();
//    }
}
