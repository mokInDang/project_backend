package mokindang.jubging.project_backend.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.CommentService;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.BoardIdResponse;
import mokindang.jubging.project_backend.comment.service.response.CommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private TokenManager tokenManager;

    @Test
    @DisplayName("입력받은 BoardType 과 boardId 에 해당하는  게시글에 새 댓글을 추가한다.")
    void addComment() throws Exception {
        //given
        when(commentService.addComment(anyLong(), any(BoardType.class), anyLong(), any(CommentCreationRequest.class)))
                .thenReturn(new BoardIdResponse(1L));

        CommentCreationRequest commentCreationRequest = new CommentCreationRequest("댓글 예시입니다.");

        //when
        ResultActions actual = mockMvc.perform(post("/api/{board-type}/{boardId}/comments",
                "recruitment-board", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreationRequest)));

        //then
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(1L));
    }

    @Test
    @DisplayName("입력받은 BoardType 과 boardId 에 해당하는 게시글에 새 댓글을 추가한다.")
    void selectComments() throws Exception {
        //given
        CommentSelectionResponse commentSelectionResponse1 = new CommentSelectionResponse(createMockedComment(), 1L);
        when(commentService.selectComments(anyLong(), any(BoardType.class), anyLong()))
                .thenReturn(new MultiCommentSelectionResponse(List.of(commentSelectionResponse1)));


        //when
        ResultActions actual = mockMvc.perform(get("/api/{board-type}/{boardId}/comments",
                "recruitment-board", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].commentId").value(1))
                .andExpect(jsonPath("$.comments[0].commentBody").value("본문내용"))
                .andExpect(jsonPath("$.comments[0].mine").value(true))
                .andExpect(jsonPath("$.comments[0].createdDatetime").value("2023-11-11T11:11:01"))
                .andExpect(jsonPath("$.comments[0].writerAlias").value("댓글작성자"))
                .andExpect(jsonPath("$.comments[0].firstFourLettersOfEmail").value("test"))
                .andExpect(jsonPath("$.comments[0].writerProfileImageUrl").value("test_url"));

    }

    private Comment createMockedComment() {

        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(1L);
        when(comment.getCommentBody()).thenReturn(new CommentBody("본문내용"));
        when(comment.isSameWriterId(any())).thenReturn(true);
        when(comment.getCreatedDateTime()).thenReturn(LocalDateTime.of(2023, 11, 11, 11, 11, 1));
        when(comment.getWriterAlias()).thenReturn("댓글작성자");
        when(comment.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(comment.getWriterProfileImageUrl()).thenReturn("test_url");
        return comment;
    }

    @Test
    @DisplayName("댓글 삭제 요청 시 , 입력 받은 commentId 에 해당하는 댓글을 삭제한 후 HTTP 상태코드 204 를 반환한다.")
    void deleteComment() throws Exception {
        //given
        doNothing().when(commentService)
                .deleteComment(anyLong(),anyLong());

        //when
        ResultActions actual = mockMvc.perform(delete("/api/comments/{commentId}",
                1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 요청 시 ,입력 받은 commentId 가 존재하지 않는 comment 인 경우 HTTP 상태코드 400을 반환한다.")
    void deleteFailedByNoneExistComment() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 댓글 입니다.")).when(commentService)
                .deleteComment(anyLong(), anyLong());

        //when
        ResultActions actual = mockMvc.perform(delete("/api/comments/{commentId}",
                1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 댓글 입니다."));
    }
}
