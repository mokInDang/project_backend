package mokindang.jubging.project_backend.service.board.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;

@Getter
public class SummaryBoardResponse {

    @Schema(description = "게시글 번호", example = "1")
    private final Long boardId;

    @Schema(description = "게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @Schema(description = "게시글 본문", example = "게시글 본문 입니다.")
    private final String contentBody;

    @Schema(description = "작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "작성자 프로필 url")
    private final String writerProfileUrl;

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

    public SummaryBoardResponse(final RecruitmentBoard recruitmentBoard) {
        this.boardId = recruitmentBoard.getId();
        this.title = recruitmentBoard.getTitle()
                .getValue();
        this.contentBody = recruitmentBoard.getContentBody()
                .getValue();
        this.writerAlias = recruitmentBoard.getWriterAlias();
        this.writerProfileUrl = recruitmentBoard.getWriterProfileImageUrl();
        this.startingDate = recruitmentBoard.getStartingDate()
                .getValue();
        this.region = recruitmentBoard.getWritingRegion()
                .getValue();
        this.activityCategory = recruitmentBoard.getActivityCategory()
                .getValue();
        this.onRecruitment = recruitmentBoard.isOnRecruitment();
        this.firstFourLettersOfEmail = recruitmentBoard.getFirstFourDigitsOfWriterEmail();
    }
}
