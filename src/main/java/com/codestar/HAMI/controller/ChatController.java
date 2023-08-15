package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.model.ChatModel;
import com.codestar.HAMI.repository.ChatRepository;
import com.codestar.HAMI.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    ChatRepository chatRepository;

    @GetMapping("/get/{profileId}")
    public List<ChatModel> getChats(@PathVariable Long profileId) {
        List<Chat> chats = chatService.getAllChats(profileId);
        List<ChatModel> chatModels = new ArrayList<>();
        for(Chat chat : chats) {
            chatModels.add(ChatModel.builder()
                            .chatId(chat.getId())
                            .bio(chat.getBio())
                            .chatType(chat.getChatType())
                            .description(chat.getDescription())
                            .photo(chat.getPhoto())
                            .build());
        }
        return chatModels;

    }

    @GetMapping("{chatId}")
    public ChatModel getChat(@PathVariable long chatId) {
        Chat chat = chatService.getChatById(chatId);
        if(chat == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found chat by chatId");
        }
        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PutMapping("{chatId}")
    public ChatModel updateChat(@PathVariable Long chatId,@RequestBody Chat chatDetail) {
        Chat chat  = chatService.updateChat(chatId, chatDetail);
        if(chat == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat id doesn't exist!");
        }

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/create")
    public ChatModel createChat(@Valid @RequestBody Chat chat) {
        Chat createChat = chatService.createChat(chat);
        return ChatModel.builder()
                .chatId(createChat.getId())
                .bio(createChat.getBio())
                .chatType(createChat.getChatType())
                .description(createChat.getDescription())
                .photo(createChat.getPhoto())
                .build();
    }

}
