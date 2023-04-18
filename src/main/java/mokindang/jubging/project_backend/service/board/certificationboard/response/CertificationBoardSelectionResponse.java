package mokindang.jubging.project_backend.service.board.certificationboard.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CertificationBoardSelectionResponse {

    @Schema(description = "게시글 번호", example = "1")
    private final Long boardId;

    @Schema(description = "인증 게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @Schema(description = "인증 게시글 본문", example = "게시글 본문 입니다.")
    private final String contentBody;

    @Schema(description = "인증 게시글 작성 일시")
    private final LocalDateTime creatingDatetime;

    @Schema(description = "인증 게시글 마지막 수정일")
    private final LocalDateTime modifiedTime;

    @Schema(description = "인증 작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "인증 게시글 작성자의 이메일 앞 4글자", example = "test")
    private final String firstFourLettersOfEmail;

    @Schema(description = "인증 게시글 작성자의 프로필 경로", example = "https://example-profile-image.png")
    private final String writerProfileImageUrl;

    @Schema(description = "인증 게시글 이미지")
    private final List<String> certificationBoardImagesUrl;

    @Schema(description = "인증 게시글 조회 회원이, 작성자인지에 대한 정보", allowableValues = {"true", "false"})
    private final boolean mine;

    public CertificationBoardSelectionResponse(final CertificationBoard board, final List<String> findImagesUrl, final boolean mine) {
        this.boardId = board.getId();
        this.title = board.getTitle()
                .getValue();
        this.contentBody = board.getContentBody()
                .getValue();
        this.creatingDatetime = board.getCreatedDateTime();
        this.modifiedTime = board.getModifiedTIme();
        this.writerAlias = board.getWriterAlias();
        this.firstFourLettersOfEmail = board.getFirstFourDigitsOfWriterEmail();
        this.writerProfileImageUrl = board.getWriterProfileImageUrl();
        this.certificationBoardImagesUrl = findImagesUrl;
        this.mine = mine;
    }
}
