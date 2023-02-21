package mokindang.jubging.project_backend.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.service.board.BoardService;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
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
    @DisplayName("새 게시글을 작성한 후, HTTP 201 상태코드를 반환한다.")
    void write() throws Exception {
        //given
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11));

        //when
        ResultActions actual = mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardCreateRequest)));

        //then
        actual.andExpect(status().isCreated());
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
}
