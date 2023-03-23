package mokindang.jubging.project_backend.service.board.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardSelectResponse {

    @Schema(description = "게시글 번호", example = "1")
    private Long boardId;

    @Schema(description = "게시글 제목", example = "예시 제목 입니다.")
    private String title;

    @Schema(description = "게시글 본문", example = "게시글 본문 입니다.")
    private String content;

    @Schema(description = "작성자", example = "작성자닉네임")
    private String writerAlias;

    @Schema(description = "활동 예정일", example = "2023-12-11")
    private String startingDate;

    @Schema(description = "지역", example = "동작구")
    private String region;

    @Schema(description = "활동 종류", example = "달리기", allowableValues = {"달리기", "산책"})
    private String activityCategory;

    @Schema(description = "모집 여부")
    private boolean onRecruitment;

    @Schema(description = "게시글 작성자의 이메일 앞 4글자")
    private String firstFourLettersOfEmail;

    @Schema(description = "게시글 조회 회원이, 작성자인지에 대한 정보", allowableValues = {"true", "false"})
    private boolean isWriter;
}
