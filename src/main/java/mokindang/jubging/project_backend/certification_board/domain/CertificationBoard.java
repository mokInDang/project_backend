package mokindang.jubging.project_backend.certification_board.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.image.domain.Image;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificationBoard {

    @Id
    @Column(name = "certification_board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTIme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Embedded
    private Title title;

    @Embedded
    private ContentBody contentBody;

    @OneToMany(mappedBy = "certificationBoard")
    private List<Image> images = new ArrayList<>();

    public CertificationBoard(final LocalDateTime createdDateTime, final LocalDateTime modifiedTIme,
                              final Member writer, final String title, final String contentBody) {
        this.createdDateTime = createdDateTime;
        this.modifiedTIme = modifiedTIme;
        this.writer = writer;
        this.title = new Title(title);
        this.contentBody = new ContentBody(contentBody);
    }

    public boolean isSameWriterId(final Long memberId) {
        return writer.getId()
                .equals(memberId);
    }

    public String getWriterProfileImageUrl() {
        return writer.getProfileImage()
                .getProfileImageUrl();
    }

    public String getWriterAlias() {
        return writer.getAlias();
    }

    public String getFirstFourDigitsOfWriterEmail() {
        return writer.getFirstFourDigitsOfWriterEmail();
    }

    public String getMainImageUrl(){
        return images.get(0).getFilePath();
    }

    public void validatePermission(final Long memberId) {
        if (!isSameWriterId(memberId)) {
            throw new ForbiddenException("작성자 권한이 없습니다.");
        }
    }

    public void modify(final Long memberId, final String titleValue, final String contentBody) {
        validatePermission(memberId);
        this.modifiedTIme = LocalDateTime.now();
        this.title = new Title(titleValue);
        this.contentBody = new ContentBody(contentBody);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CertificationBoard certificationBoard = (CertificationBoard) o;
        return Objects.equals(id, certificationBoard.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
