package com.codestar.HAMI.elasticsearch.service;

import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ChatElasticRepository;
import com.codestar.HAMI.entity.Chat;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class ChatElasticService {

//    private final String INDEX_NAME = "chat";
//
//    @Autowired @Qualifier("chatElastic")
//    ChatElasticRepository chatElasticRepository;
//
//    @Autowired
//    ElasticsearchOperations elasticsearchOperations;
//
//    public void addChatToIndex(Chat chat){
//        ChatElasticModel chatElasticModel = ChatElasticModel
//                .builder()
//                .id(chat.getId())
//                .username(chat.getName())
//                .build();
//        IndexQuery query = new IndexQueryBuilder()
//                .withId(chatElasticModel.getId().toString())
//                .withObject(chatElasticModel)
//                .build();
//        elasticsearchOperations.index(query, IndexCoordinates.of(INDEX_NAME));
//    }
//
//    public void removeChatFromIndex(Chat chat){
//        chatElasticRepository.deleteById(chat.getId());
//    }
//
//    public List<ChatElasticModel> matchChatsWithUsername(String fieldValue) throws IOException {
//        QueryBuilder query = QueryBuilders
//                .matchQuery("username", fieldValue)
//                .fuzziness(Fuzziness.AUTO);
//
//        Query searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(query)
//                .build();
//
//        SearchHits<ChatElasticModel> searchHits =
//                elasticsearchOperations.search(searchQuery, ChatElasticModel.class, IndexCoordinates.of(INDEX_NAME));
//
//        return searchHits.stream()
//                .map(SearchHit::getContent)
//                .collect(Collectors.toList());
//    }
}
