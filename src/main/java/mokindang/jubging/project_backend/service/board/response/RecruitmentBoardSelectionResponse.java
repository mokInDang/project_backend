package mokindang.jubging.project_backend.service.board.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;

import java.time.LocalDateTime;

@Getter
public class RecruitmentBoardSelectionResponse {

    @Schema(description = "게시글 번호", example = "1")
    private final Long boardId;

    @Schema(description = "게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @Schema(description = "게시글 본문", example = "게시글 본문 입니다.")
    private final String content;

    @Schema(description = "게시글 작성 일시")
    private final LocalDateTime creatingDatetime;

    @Schema(description = "작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "활동 예정일", example = "2023-12-11")
    private final String startingDate;

    @Schema(description = "지역", example = "동작구")
    private final String region;

    @Schema(description = "활동 종류", example = "달리기", allowableValues = {"달리기", "산책"})
    private final String activityCategory;

    @Schema(description = "모집 여부")
    private final boolean onRecruitment;

    @Schema(description = "게시글 작성자의 이메일 앞 4글자")
    private final String firstFourLettersOfEmail;

    @Schema(description = "게시글 작성자의 프로필 경로")
    private final String writerProfileImageUrl;

    @Schema(description = "게시글 조회 회원이, 작성자인지에 대한 정보", allowableValues = {"true", "false"})
    private final boolean mine;

    public RecruitmentBoardSelectionResponse(final RecruitmentBoard board, final boolean mine) {
        this.boardId = board.getId();
        this.title = board.getTitle()
                .getValue();
        this.content = board.getContent()
                .getValue();
        this.creatingDatetime = board.getCreatingDateTime();
        this.writerAlias = board.getWriterAlias();
        this.startingDate = board.getStartingDate()
                .getValue();
        this.region = board.getWritingRegion()
                .getValue();
        this.activityCategory = board.getActivityCategory()
                .getValue();
        this.onRecruitment = board.isOnRecruitment();
        this.firstFourLettersOfEmail = board.getFirstFourDigitsOfWriterEmail();
        this.writerProfileImageUrl = board.getWriterProfileImageUrl();
        this.mine = mine;
    }
}
