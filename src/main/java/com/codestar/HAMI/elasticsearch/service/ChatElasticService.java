package com.codestar.HAMI.elasticsearch.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ChatElasticRepository;
import com.codestar.HAMI.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatElasticService {

    private final String INDEX_NAME = "chat";

//    @Autowired
//    ChatElasticRepository chatElasticRepository;
//
//    public void addChatToIndex(Chat chat) throws IOException {
//        ChatElasticModel chatElasticModel = ChatElasticModel
//                .builder()
//                .id(chat.getId())
//                .username(chat.getName())
//                .build();
//        chatElasticRepository.createOrUpdate(chatElasticModel);
//    }
//
//    public void removeChatFromIndex(Chat chat) throws IOException {
//        chatElasticRepository.deleteById(chat.getId());
//    }
//
//    public List<ChatElasticModel> matchChatsWithUsername(String fieldValue) throws IOException{
//        List<Hit<ChatElasticModel>> listOfHits = chatElasticRepository.searchWithFuzziness(fieldValue);
//        List<ChatElasticModel> chatElasticModels  = new ArrayList<>();
//        for(Hit<ChatElasticModel> hit : listOfHits){
//            chatElasticModels.add(hit.source());
//        }
//        return chatElasticModels;
//    }
}
