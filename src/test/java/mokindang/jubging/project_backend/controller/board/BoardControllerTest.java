package mokindang.jubging.project_backend.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.service.board.BoardService;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private TokenManager tokenManager;

    @Test
    @DisplayName("새 게시글을 작성한 후, 작성된 게시글의 id 값과 함께 HTTP 201 상태코드를 반환한다.")
    void write() throws Exception {
        //given
        when(boardService.write(anyLong(), any(BoardCreateRequest.class))).thenReturn(new BoardIdResponse(1L));

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardCreateRequest)));

        //then
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(1L));
    }

    @Test
    @DisplayName("새 게시글 작성 시, 유저가 존재하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByNonexistentMember() throws Exception {
        //given
        doThrow(new IllegalArgumentException("해당하는 유저가 존재하지 않습니다.")).when(boardService)
                .write(anyLong(), any(BoardCreateRequest.class));

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardCreateRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("해당하는 유저가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("새 게시글 작성 시, 입력 받은 제목이 유효하지 않으면 HTTP 400 을 반환한다.")
    void writeFailedByIncorrectTitle() throws Exception {
        //given
        doThrow(new IllegalArgumentException("글 제목은 1글자 이상 140자 이하야합니다.")).when(boardService)
                .write(anyLong(), any(BoardCreateRequest.class));

        BoardCreateRequest incorrectTitleRequest = new BoardCreateRequest("잘못된 제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
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
                .write(anyLong(), any(BoardCreateRequest.class));

        BoardCreateRequest incorrectContentRequest = new BoardCreateRequest("제목", "잘못된 본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
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
                .write(anyLong(), any(BoardCreateRequest.class));

        BoardCreateRequest incorrectContentRequest = new BoardCreateRequest("제목", "잘못된 본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incorrectContentRequest)));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("이미 지난 날짜는 활동 시작일로 할 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 식별 번호를 입력받아 게시글 조회 시, 유저의 지역과 게시글의 지역이 일치한 경우 HTTP 200 과" +
            " 게시글 제목, 본문, 작성자, 활동 지역, 활동 예정일 활동 종류, 모집 여부 를 담은 BoardSelectResponse 를 반환한다.")
    void select() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 30, 11, 11);
        BoardSelectResponse boardSelectResponse = new BoardSelectResponse(1L, "제목", "본문", now, "작성자",
                "2023-03-10", "동작구", "달리기", true, "test", true);
        when(boardService.select(anyLong(), anyLong())).thenReturn(boardSelectResponse);

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("boardId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("본문"))
                .andExpect(jsonPath("$.writerAlias").value("작성자"))
                .andExpect(jsonPath("$.startingDate").value("2023-03-10"))
                .andExpect(jsonPath("$.region").value("동작구"))
                .andExpect(jsonPath("$.activityCategory").value("달리기"))
                .andExpect(jsonPath("$.onRecruitment").value(true))
                .andExpect(jsonPath("$.firstFourLettersOfEmail").value("test"))
                .andExpect(jsonPath("$.mine").value(true))
                .andExpect(jsonPath("$.creatingDatetime").value("2023-03-30T11:11:00"));
    }

    @Test
    @DisplayName("게시글 식별 번호를 입력받아 게시글 조회 시, 유저의 지역과 게시글의 지역이 일치하지 않으면 HTTP 400 상태코드와 함께 예외를 반환한다.")
    void failedToMatchMemberRegionAndBoardArea() throws Exception {
        //given
        doThrow(new IllegalArgumentException("지역이 다른 유저는 게시글에 접근 할 수 없습니다.")).when(boardService)
                .select(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(get("/api/boards/{boardId}", 1L)
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
        ResultActions actual = mockMvc.perform(get("/api/boards/{boardId}", 1L)
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
        when(boardService.delete(anyLong(), anyLong())).thenReturn(new BoardIdResponse(1L));

        //when
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{boardId}", 1L)
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
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{boardId}", 1L)
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
        ResultActions actual = mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 게시물입니다."));
    }
}
