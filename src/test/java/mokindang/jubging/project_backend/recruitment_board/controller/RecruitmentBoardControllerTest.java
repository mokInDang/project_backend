package mokindang.jubging.project_backend.recruitment_board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.facade.OptimisticLockRecruitmentBoardResolver;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.MultiBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.SummaryBoardResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.BoardPlaceMarkerResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.MultiBoardPlaceSelectionResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static mokindang.jubging.project_backend.member.MockedMemberFactory.createMockedMember;
import static mokindang.jubging.project_backend.recruitment_board.service.MockedRecruitmentBoardFactory.createMockedRecruitmentBoard;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecruitmentBoardController.class)
class RecruitmentBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecruitmentBoardService boardService;

    @MockBean
    private OptimisticLockRecruitmentBoardResolver optimisticLockRecruitmentBoardResolver;

    @MockBean
    private TokenManager tokenManager;

    @Test
    @DisplayName("새 게시글을 작성한 후, 작성된 게시글의 id 값과 함께 HTTP 201 상태코드를 반환한다.")
    void write() throws Exception {
        //given
        when(boardService.write(anyLong(), any(RecruitmentBoardCreationRequest.class))).thenReturn(new RecruitmentBoardIdResponse(1L));

        RecruitmentBoardCreationRequest boardCreationRequest = createTestRecruitmentBoardCreationRequest();

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards/recruitment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardCreationRequest)));

        //then
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(1L));
    }

    private RecruitmentBoardCreationRequest createTestRecruitmentBoardCreationRequest() {
        MeetingPlaceCreationRequest meetingPlaceModificationRequest = createTestMeetingPlaceCreationRequest();
        return new RecruitmentBoardCreationRequest("제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11), meetingPlaceModificationRequest, 8);
    }

    private MeetingPlaceCreationRequest createTestMeetingPlaceCreationRequest() {
        return new MeetingPlaceCreationRequest(1.1, 1.2, "서울시 동작구 상도동 1-1");
    }

    @Test
    @DisplayName("새 게시글 작성 시, 유저가 존재하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByNonexistentMember() throws Exception {
        //given
        doThrow(new IllegalArgumentException("해당하는 유저가 존재하지 않습니다.")).when(boardService)
                .write(anyLong(), any(RecruitmentBoardCreationRequest.class));

        RecruitmentBoardCreationRequest boardCreationRequest = createTestRecruitmentBoardCreationRequest();

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards/recruitment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardCreationRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("해당하는 유저가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("새 게시글 작성 시, 입력 받은 제목이 유효하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByIncorrectTitle() throws Exception {
        //given
        doThrow(new IllegalArgumentException("글 제목은 1글자 이상 140자 이하야합니다.")).when(boardService)
                .write(anyLong(), any(RecruitmentBoardCreationRequest.class));

        RecruitmentBoardCreationRequest incorrectTitleRequest = createTestRecruitmentBoardCreationRequest();

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards/recruitment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incorrectTitleRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("글 제목은 1글자 이상 140자 이하야합니다."));
    }

    @Test
    @DisplayName("새 게시글 작성 시, 입력받은 본문이 유효하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByIncorrectContent() throws Exception {
        //given
        doThrow(new IllegalArgumentException("글 내용은 최소 1자 이상, 최대 4000자 입니다.")).when(boardService)
                .write(anyLong(), any(RecruitmentBoardCreationRequest.class));

        RecruitmentBoardCreationRequest incorrectContentRequest = createTestRecruitmentBoardCreationRequest();

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards/recruitment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incorrectContentRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("글 내용은 최소 1자 이상, 최대 4000자 입니다."));
    }

    @Test
    @DisplayName("새 게시글 작성 시, 입력받은 활동 날짜가 유효하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByIncorrectStartingDate() throws Exception {
        //given
        doThrow(new IllegalArgumentException("이미 지난 날짜는 활동 시작일로 할 수 없습니다.")).when(boardService)
                .write(anyLong(), any(RecruitmentBoardCreationRequest.class));

        RecruitmentBoardCreationRequest incorrectContentRequest = createTestRecruitmentBoardCreationRequest();

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards/recruitment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incorrectContentRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("이미 지난 날짜는 활동 시작일로 할 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 식별 번호를 입력받아 게시글 조회 시, 유저의 지역과 게시글의 지역이 일치한 경우 HTTP 200 과" +
            "게시글 조회 정보를 담은 BoardSelectResponse 를 반환한다.")
    void select() throws Exception {
        //given
        RecruitmentBoardSelectionResponse boardSelectionResponse = new RecruitmentBoardSelectionResponse(createMockedMember(1L), createMockedRecruitmentBoard(1L));
        when(boardService.select(anyLong(), anyLong())).thenReturn(boardSelectionResponse);

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.contentBody").value("본문내용"))
                .andExpect(jsonPath("$.writerAlias").value("test"))
                .andExpect(jsonPath("$.startingDate").value("2025-02-11"))
                .andExpect(jsonPath("$.region").value("동작구"))
                .andExpect(jsonPath("$.activityCategory").value("달리기"))
                .andExpect(jsonPath("$.onRecruitment").value(true))
                .andExpect(jsonPath("$.writerProfileImageUrl").value("test_url"))
                .andExpect(jsonPath("$.firstFourLettersOfEmail").value("test"))
                .andExpect(jsonPath("$.mine").value(true))
                .andExpect(jsonPath("$.creatingDatetime").value("2023-11-12T00:00:00"))
                .andExpect(jsonPath("$.meetingPlaceResponse.longitude").value(1.1))
                .andExpect(jsonPath("$.meetingPlaceResponse.latitude").value(1.2))
                .andExpect(jsonPath("$.meetingPlaceResponse.meetingAddress").value("서울시 동작구 상도동 1-1"));
    }


    @Test
    @DisplayName("게시글 식별 번호를 입력받아 게시글 조회 시, 유저의 지역과 게시글의 지역이 일치하지 않으면 HTTP 400 상태코드와 함께 예외를 반환한다.")
    void failedToMatchMemberRegionAndBoardArea() throws Exception {
        //given
        doThrow(new IllegalArgumentException("지역이 다른 유저는 게시글에 접근 할 수 없습니다.")).when(boardService)
                .select(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("지역이 다른 유저는 게시글에 접근 할 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 식별 번호를 입력받아 게시글 조회 시, 존재하지 않는 게시글이면 400 상태코드와 함께 예외를 반환한다.")
    void failedByNonexistentBoard() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 게시물입니다.")).when(boardService)
                .select(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 게시물입니다."));
    }

    @Test
    @DisplayName("게시글 삭제 요청 시, 요청 회원이 작성자인지 확인하고 게시글을 삭제한다. " +
            "삭제 후 Http 200 코드와 삭제된 게시글 id 를 담은 BoardIdResponse 를 반환한다.")
    void delete() throws Exception {
        //given
        when(boardService.delete(anyLong(), anyLong())).thenReturn(new RecruitmentBoardIdResponse(1L));

        //when
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1L));
    }

    @Test
    @DisplayName("게시글 삭제 요청 시, 요청 회원이 작성자가 아닌경우 예외를 반환하고, Http 401을 반환한다.")
    void deleteFailedByNoneMatchingBoardWhitWritingMember() throws Exception {
        //given
        doThrow(new ForbiddenException("글 작성자만 게시글을 삭제할 수 있습니다.")).when(boardService)
                .delete(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("글 작성자만 게시글을 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 대한 삭제 요청일 경우, Http 400 상태코드와 에러 메세지를 반환한다.")
    void deleteFailedByNonexistentBoard() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 게시물입니다.")).when(boardService)
                .delete(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 게시물입니다."));
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 요청 받은 게시글의 활동 시작일, 활동 종류, 제목, 본문을 변경한다.")
    void modify() throws Exception {
        //given
        RecruitmentBoardIdResponse boardIdResponse = new RecruitmentBoardIdResponse(1L);
        when(boardService.modify(anyLong(), anyLong(), any(BoardModificationRequest.class))).thenReturn(boardIdResponse);

        BoardModificationRequest boardModificationRequest = createTestModificationRequest();

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardModificationRequest)));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1));
    }

    private BoardModificationRequest createTestModificationRequest() {
        MeetingPlaceModificationRequest meetingPlaceModificationRequest = new MeetingPlaceModificationRequest(1.1, 1.2, "서울시 동작구 상도동 1-1");

        return new BoardModificationRequest("새로운 제목", "새로운 본문",
                "산책", LocalDate.of(2023, 1, 1), meetingPlaceModificationRequest);
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 존재하지 않는 게시글에 대한 수정을 요청할 시 HTTP 400 과 예외를 담은 ErrorResponse 를 반환한다.")
    void modifyFailedByNonexistentBoard() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 게시물입니다.")).when(boardService)
                .modify(anyLong(), anyLong(), any(BoardModificationRequest.class));

        BoardModificationRequest boardModificationRequest = createTestModificationRequest();

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardModificationRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 게시물입니다."));
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 게시글의 글작성자가 아닌 경우 HTTP 403 과 예외를 반환한다.")
    void modifyFailedByNoneExistBoard() throws Exception {
        //given
        doThrow(new ForbiddenException("글 작성자만 게시글을 수정할 수 있습니다.")).when(boardService)
                .modify(anyLong(), anyLong(), any(BoardModificationRequest.class));

        BoardModificationRequest boardModificationRequest = createTestModificationRequest();

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardModificationRequest)));

        //then
        actual.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("글 작성자만 게시글을 수정할 수 있습니다."));
    }

    @Test
    @DisplayName("게시글 모집 마감 요청 시, 게시글 모집 마감 완료 후 Http 200 과 마감 게시글 id 를 담은 BoardIdResponse 를 반환한다.")
    void closeRecruitment() throws Exception {
        //given
        when(boardService.closeRecruitment(anyLong(), anyLong())).thenReturn(new RecruitmentBoardIdResponse(1L));

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}/recruitment-status", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1));
    }

    @Test
    @DisplayName("게시글 마감 요청 시, 존재하지 않는 게시글에 대한 마감을 요청할 시 HTTP 400 과 예외를 담은 ErrorResponse 를 반환한다.")
    void closeRecruitmentFailedByNoneExistBoard() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 게시물입니다.")).when(boardService)
                .closeRecruitment(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}/recruitment-status", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 게시물입니다."));
    }

    @Test
    @DisplayName("게시글 마감 요청 시, 글 작성자가 아닌 회원이 게시글 마감을 요청한 경우 HTTP 403 과 예외를 담은 ErrorResponse 를 반환한다.")
    void closeRecruitmentFailedByNoneMatchingBoardWhitWritingMember() throws Exception {
        //given
        doThrow(new ForbiddenException("글 작성자만 모집 마감할 수 있습니다.")).when(boardService)
                .closeRecruitment(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}/recruitment-status", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("글 작성자만 모집 마감할 수 있습니다."));
    }

    @Test
    @DisplayName("특정 지역에 해당하는 구인 게시글 조회 시, HTTP 200 코드와 함께 요청 회원 지역에 해당하는 게시글 리스트를 반환한다.")
    void selectRegionBoards() throws Exception {
        //given
        List<SummaryBoardResponse> summaryBoardResponses = List.of(new SummaryBoardResponse(createMockedRecruitmentBoard(1L), 1L),
                new SummaryBoardResponse(createMockedRecruitmentBoard(1L), 0L));
        when(boardService.selectRegionBoards(anyLong(), any(Pageable.class)))
                .thenReturn(new MultiBoardSelectionResponse(summaryBoardResponses, false));

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/recruitment/region").param("page", "0")
                .param("size", "2"));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boards").exists())
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("특정 지역에 해당하는 게시글리스트 조회 시, HTTP 200 와 함께 게시글의 장소 값(마커)과 id 를 갖고 있는 BoardPlaceMarkerResponse 리스트를 반환한다.")
    void selectPlacesOfRegionBoards() throws Exception {
        //given
        List<BoardPlaceMarkerResponse> boardPlaceMarkerResponses = List.of(new BoardPlaceMarkerResponse(createMockedRecruitmentBoard(2L)),
                new BoardPlaceMarkerResponse(createMockedRecruitmentBoard(1L)));
        when(boardService.selectRegionBoardsCloseToDeadline(anyLong(), any(Pageable.class)))
                .thenReturn(new MultiBoardPlaceSelectionResponse(boardPlaceMarkerResponses, false));

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/recruitment/places").param("page", "0")
                .param("size", "2"));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardPlaceMarkerResponses").exists())
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("구인 게시글에 참여요청 시, HTTP 200 과 함께 게시글의 id 를 가진 RecruitmentBoardIdResponse 를 반환한다.")
    void participate() throws Exception {
        //given
        when(optimisticLockRecruitmentBoardResolver.participate(anyLong(), anyLong())).thenReturn(new RecruitmentBoardIdResponse(1L));

        //when
        ResultActions actual = mockMvc.perform(patch("/api/boards/recruitment/{boardId}/participation-list", 1L));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1L));
    }
}
