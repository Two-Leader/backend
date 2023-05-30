package com.twoleader.backend.domain.user.mapper;

import com.twoleader.backend.domain.studyRoom.entity.StudyRoom;
import com.twoleader.backend.domain.user.dto.request.CreateUserRequest;
import com.twoleader.backend.domain.user.dto.response.GetUserResponse;
import com.twoleader.backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

  public User toEntity(CreateUserRequest request, StudyRoom studyRoom) {
    return User.builder().userName(request.getUserName()).studyRoom(studyRoom).build();
  }

  public GetUserResponse toDto(User user) {
    return GetUserResponse.builder()
        .userUuid(user.getUserUuid())
        .userName(user.getUserName())
        .build();
  }

  public List<GetUserResponse> toDto(List<User> users){
    return users.stream().map(this::toDto).collect(Collectors.toList());
  }
}
