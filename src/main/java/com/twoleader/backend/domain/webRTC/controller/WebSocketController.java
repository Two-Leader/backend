package com.twoleader.backend.domain.webRTC.controller;

import com.twoleader.backend.domain.roomUser.service.RoomUserService;
import com.twoleader.backend.domain.webRTC.dto.request.ChatMessageRequest;
import com.twoleader.backend.domain.webRTC.service.SendMessagingService;
import com.twoleader.backend.global.result.WebSocket.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

import static com.twoleader.backend.global.result.WebSocket.OutputMessageCode.WEBSOCKET_SUCCESS_CHAT;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
  private final SendMessagingService stompMessagingService;
  private final RoomUserService roomUserService;

  @MessageMapping("/join/{userUuid}")
  public void addUser(@DestinationVariable String userUuid, SimpMessageHeaderAccessor headerAccessor) {
    log.info("[ws] join User : {}",userUuid);
    // Add username in web socket session
    headerAccessor.getSessionAttributes().put("userUuid", userUuid);
  }
  @MessageMapping("/chat/{roomUuid}")
  public void chatMessage(@Payload ChatMessageRequest message, @DestinationVariable("roomUuid") String roomUuid){
    log.info("[ws] chatMessage : roomUuid {}, message {}, sender {}",roomUuid,message.getUserName(),message.getUserName());
    stompMessagingService.sendToUser(roomUuid, new OutputMessage<>(WEBSOCKET_SUCCESS_CHAT,message));
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccesor = StompHeaderAccessor.wrap(event.getMessage());
    UUID userUuid = UUID.fromString(headerAccesor.getSessionAttributes().get("userUuid").toString());
    log.info("[ws] sessionId Disconnected : {}",userUuid);
    roomUserService.deleteUserByUuid(userUuid);
  }
}
