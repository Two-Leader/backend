package com.twoleader.backend.domain.studyRoom.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.twoleader.backend.domain.studyRoom.dto.request.CreateStudyRoomRequest;
import com.twoleader.backend.domain.studyRoom.dto.response.GetStudyRoomResponse;
import com.twoleader.backend.domain.studyRoom.entity.StudyRoom;
import com.twoleader.backend.domain.studyRoom.exception.NotFoundStudyRoom;
import com.twoleader.backend.domain.studyRoom.mapper.StudyRoomMapper;
import com.twoleader.backend.domain.studyRoom.repository.StudyRoomRepository;
import com.twoleader.backend.domain.roomUser.entity.RoomUser;
import com.twoleader.backend.domain.roomUser.repository.RoomUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
public class StudyRoomServiceTest {
  @Mock private StudyRoomRepository studyRoomRepository;
  @Mock private RoomUserRepository roomUserRepository;

  @InjectMocks private StudyRoomService studyRoomService;

  @Spy private StudyRoomMapper studyRoomMapper;

  private List<StudyRoom> studyRooms = new ArrayList<>();
  private List<RoomUser> users = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    studyRooms.add(
        StudyRoom.builder()
            .roomId(1L)
            .roomUuid(UUID.randomUUID())
            .roomName("testStudyRoom1")
            .build());
    studyRooms.add(
        StudyRoom.builder()
            .roomId(2L)
            .roomUuid(UUID.randomUUID())
            .roomName("testStudyRoom2")
            .build());

    users.add(
        RoomUser.builder()
            .userId(1L)
            .userUuid(UUID.randomUUID())
            .userName("tester")
            .studyRoom(studyRooms.get(0))
            .build());
  }

  @Test
  @DisplayName("StudyRoom 저장")
  public void createStudyRoomTest() {
    // given
    StudyRoom studyRoom = studyRooms.get(0);
    CreateStudyRoomRequest createStudyRoomDto =
        CreateStudyRoomRequest.builder().roomName(studyRoom.getRoomName()).build();
    given(studyRoomRepository.save(any())).willReturn(studyRoom);

    // when
    GetStudyRoomResponse response = studyRoomService.createStudyRoom(createStudyRoomDto);

    // then
    assertEquals(studyRoom.getRoomName(), response.getRoomName());
    assertEquals(studyRoom.getRoomUuid(), response.getRoomUuid());
  }

  @Test
  @DisplayName("StudyRoom 모두 조회")
  public void findAllStudyRoomTest() {
    // given
    given(studyRoomRepository.findAll()).willReturn(studyRooms);
    int index = 0;

    // when
    List<GetStudyRoomResponse> findStudyRooms = studyRoomService.findAllStudyRoom();

    // then
    assertEquals(studyRooms.size(), findStudyRooms.size());
    assertEquals(studyRooms.get(index).getRoomUuid(), findStudyRooms.get(index).getRoomUuid());
    assertEquals(studyRooms.get(index).getRoomName(), findStudyRooms.get(index).getRoomName());
  }

  @Nested
  class findStudyRoomByUuid {
    @Test
    @DisplayName("존재하는 StudyRoom & User가 한명")
    public void findStudyRoomByroomUuidWhenExistAndOneUser() {
      // given
      int index = 0;
      StudyRoom studyRoom = studyRooms.get(index);

      given(studyRoomRepository.findStudyRoomByUuid(any()))
          .willReturn(Optional.ofNullable(studyRoom));
      given(roomUserRepository.findAllInStudyRoomByStudyRoomUuid(any())).willReturn(users);

      // when
      assert studyRoom != null;
      GetStudyRoomResponse response = studyRoomService.findStudyRoomByUuid(studyRoom.getRoomUuid());

      // then
      assertEquals(studyRoom.getRoomUuid(), response.getRoomUuid());
      assertEquals(studyRoom.getRoomName(), response.getRoomName());
      assertEquals(false, response.getCheckUser());
      assertEquals(users.get(index).getUserUuid(), response.getUsers().get(index).getUserUuid());
    }

    @Test
    @DisplayName("존재하는 StudyRoom & User가 두명")
    public void findStudyRoomByRoomUuidWhenExistAndTwoUser() {
      // given
      int index = 0;
      users.add(
          RoomUser.builder()
              .userId(2L)
              .userUuid(UUID.randomUUID())
              .userName("tester2")
              .studyRoom(studyRooms.get(0))
              .build());
      StudyRoom studyRoom = studyRooms.get(index);
      given(studyRoomRepository.findStudyRoomByUuid(any()))
          .willReturn(Optional.ofNullable(studyRoom));
      given(roomUserRepository.findAllInStudyRoomByStudyRoomUuid(any())).willReturn(users);

      // when
      assert studyRoom != null;
      GetStudyRoomResponse response = studyRoomService.findStudyRoomByUuid(studyRoom.getRoomUuid());

      // then
      assertEquals(studyRoom.getRoomUuid(), response.getRoomUuid());
      assertEquals(studyRoom.getRoomName(), response.getRoomName());
      assertEquals(true, response.getCheckUser());
      assertEquals(users.get(index).getUserUuid(), response.getUsers().get(index).getUserUuid());
    }

    @Test
    @DisplayName("존재하지 않는 StudyRoom")
    public void findStudyRoomByRoomUuidWhenNotExist() {
      // given
      given(studyRoomRepository.findStudyRoomByUuid(any())).willReturn(Optional.empty());

      // when, then
      assertThrows(
          NotFoundStudyRoom.class,
          () -> {
            studyRoomService.findStudyRoomByUuid(UUID.randomUUID());
          });
    }
  }
}
