package com.codestar.HAMI.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ChatElasticRepository;
import com.codestar.HAMI.elasticsearch.util.ElasticSearchUtil;
import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ChatElasticService {

    @Autowired
    ChatElasticRepository chatElasticRepository;

    @Autowired
    ElasticsearchClient elasticsearchClient;

    public void addChatToIndex(Chat chat){
        ChatElasticModel profileElasticModel = ChatElasticModel
                .builder()
                .id(chat.getId())
                .username(chat.getName())
                .build();
        chatElasticRepository.save(profileElasticModel);
    }

    public void removeChatFromIndex(Profile profile){
        ChatElasticModel profileElasticModel = ChatElasticModel
                .builder()
                .id(profile.getId())
                .build();
        chatElasticRepository.delete(profileElasticModel);
    }

    public SearchResponse<ChatElasticModel> matchChatsWithUsername(String fieldValue) throws IOException {
        Supplier<Query> supplier  = ElasticSearchUtil.supplierWithNameField(fieldValue);
        SearchResponse<ChatElasticModel> searchResponse = elasticsearchClient.search(s->s.index("chat").query(supplier.get()),ChatElasticModel.class);
        return searchResponse;
    }
}
