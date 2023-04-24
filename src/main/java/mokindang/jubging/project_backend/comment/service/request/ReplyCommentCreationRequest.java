package mokindang.jubging.project_backend.comment.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCommentCreationRequest {

    private String replyCommentBody;
}
