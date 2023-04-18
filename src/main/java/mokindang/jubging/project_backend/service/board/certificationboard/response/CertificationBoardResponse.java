package mokindang.jubging.project_backend.service.board.certificationboard.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;

@Getter
public class CertificationBoardResponse {

    @Schema(description = "인증 게시글 번호", example = "1")
    private final Long boardId;

    @Schema(description = "인증 게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @Schema(description = "인증 게시글 본문", example = "게시글 본문 입니다.")
    private final String contentBody;

    @Schema(description = "작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "작성자 프로필 url", example = "https://example-profile-image.png")
    private final String writerProfileUrl;

    @Schema(description = "인증 게시글 작성자의 이메일 앞 4글자", example = "test")
    private final String firstFourLettersOfEmail;

    @Schema(description = "인증 게시글의 메인 이미지 url", example = "https://example-main-image.png")
    private final String mainImageUrl;

    public CertificationBoardResponse(final CertificationBoard certificationBoard) {
        this.boardId = certificationBoard.getId();
        this.title = certificationBoard.getTitle()
                .getValue();
        this.contentBody = certificationBoard.getContentBody()
                .getValue();
        this.writerAlias = certificationBoard.getWriterAlias();
        this.writerProfileUrl = certificationBoard.getWriterProfileImageUrl();
        this.firstFourLettersOfEmail = certificationBoard.getFirstFourDigitsOfWriterEmail();
        this.mainImageUrl = certificationBoard.getMainImageUrl();
    }
}
