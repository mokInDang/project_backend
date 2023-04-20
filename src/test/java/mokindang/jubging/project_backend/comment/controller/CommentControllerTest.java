package mokindang.jubging.project_backend.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.comment.service.CommentService;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecruitmentBoardService recruitmentBoardService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private TokenManager tokenManager;

    @Test
    @DisplayName("입력받은 boardId 에 해당하는 recruitmentBoard 에 새 댓글을 추가한다.")
    void addCommentToRecruitmentBoard() throws Exception {
        //given
        when(commentService.addCommentToRecruitmentBoard(anyLong(), anyLong(), any(CommentCreationRequest.class))).thenReturn(new RecruitmentBoardIdResponse(1L));

        CommentCreationRequest commentCreationRequest = new CommentCreationRequest("댓글 예시입니다.");

        //when
        ResultActions actual = mockMvc.perform(post("/api/recruitment-board/{boardId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreationRequest)));

        //then
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(1L));
    }
}
