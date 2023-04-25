package mokindang.jubging.project_backend.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.domain.ReplyComment;
import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.CommentService;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.request.ReplyCommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.*;
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
    @DisplayName("입력받은 BoardType 과 boardId 에 해당하는 댓글과 댓글 리스트를 반환한다.")
    void selectComments() throws Exception {
        //given
        MultiReplyCommentSelectionResponse multiReplyCommentSelectionResponse = new MultiReplyCommentSelectionResponse(
                List.of(new ReplyCommentSelectionResponse(createMockedReplyComment(), 1L)));
        CommentSelectionResponse commentSelectionResponse = new CommentSelectionResponse(createMockedComment(),
                1L, multiReplyCommentSelectionResponse);
        when(commentService.selectComments(anyLong(), any(BoardType.class), anyLong()))
                .thenReturn(new MultiCommentSelectionResponse(List.of(commentSelectionResponse)));


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
                .andExpect(jsonPath("$.comments[0].writerProfileImageUrl").value("test_url"))
                .andExpect(jsonPath("$.comments[0].multiReplyCommentSelectionResponse.replyComments").exists())
                .andExpect(jsonPath("$.comments[0].multiReplyCommentSelectionResponse.countOfReplyComments").value(1));
    }

    private Comment createMockedComment() {
        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(1L);
        when(comment.getCommentBody()).thenReturn(new CommentBody("본문내용"));
        when(comment.isSameWriterId(anyLong())).thenReturn(true);
        when(comment.getCreatedDateTime()).thenReturn(LocalDateTime.of(2023, 11, 11, 11, 11, 1));
        when(comment.getWriterAlias()).thenReturn("댓글작성자");
        when(comment.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(comment.getWriterProfileImageUrl()).thenReturn("test_url");
        return comment;
    }

    private ReplyComment createMockedReplyComment() {
        ReplyComment replyComment = mock(ReplyComment.class);
        when(replyComment.getId()).thenReturn(1L);
        when(replyComment.getReplyCommentBody()).thenReturn(new CommentBody("댓글 본문"));
        when(replyComment.isSameWriterId(anyLong())).thenReturn(true);
        when(replyComment.getCreatedDateTime()).thenReturn(LocalDateTime.of(2323, 11, 11, 11, 11, 11));
        when(replyComment.getWriterAlias()).thenReturn("대댓글작성자");
        when(replyComment.getWriterProfileImageUrl()).thenReturn("test_url");
        when(replyComment.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        return replyComment;
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

    @Test
    @DisplayName("대댓글 작성 요청 시, 입력받은 commentId 에 대댓글을 작성한 후, http 상태코드 201 를 반환한다.")
    void addReplyComment() throws Exception {
        //given
        when(commentService.addReplyComment(anyLong(), anyLong(), any(ReplyCommentCreationRequest.class))).thenReturn(new CommentIdResponse(1L));

        ReplyCommentCreationRequest replyCommentCreationRequest = new ReplyCommentCreationRequest("대댓글 본문");
        //when
        ResultActions actual = mockMvc.perform(post("/api/comments/{commentId}/reply-comment", 1L)
                        .content(objectMapper.writeValueAsString(replyCommentCreationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1L));
    }

    @Test
    @DisplayName("대댓글 작성 요청 시, 입력받은 commentId 가 존재하지 않을 경우 http 상태코드 400 를 반환한다.")
    void addReplyCommentFailedByNoneExistComment() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 댓글 입니다.")).when(commentService)
                .addReplyComment(anyLong(), anyLong(), any(ReplyCommentCreationRequest.class));

        ReplyCommentCreationRequest replyCommentCreationRequest = new ReplyCommentCreationRequest("대댓글 본문");
        //when
        ResultActions actual = mockMvc.perform(post("/api/comments/{commentId}/reply-comment", 1L)
                .content(objectMapper.writeValueAsString(replyCommentCreationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 댓글 입니다."));
    }

    @Test
    @DisplayName("대댓글 삭제 요청 시 , 입력 받은 replyCommentId 에 해당하는 대댓글을 삭제한 후 HTTP 상태코드 204 를 반환한다.")
    void deleteReplyComment() throws Exception {
        //given
        doNothing().when(commentService)
                .deleteReplyComment(anyLong(),anyLong());

        //when
        ResultActions actual = mockMvc.perform(delete("/api/comments/reply-comments/{replyCommentId}",
                1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("대댓글 삭제 요청 시 , 입력 받은 replyCommentId 에 해당하는 대댓글이 존재하지 않는 경우 후 HTTP 상태코드 400 를 반환한다.")
    void deleteFailedByNoneExistReplyComment() throws Exception {
        //given
        doThrow(new IllegalArgumentException("존재하지 않는 대댓글 입니다.")).when(commentService)
                .deleteReplyComment(anyLong(),anyLong());

        //when
        ResultActions actual = mockMvc.perform(delete("/api/comments/reply-comments/{replyCommentId}",
                1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 대댓글 입니다."));
    }

}
