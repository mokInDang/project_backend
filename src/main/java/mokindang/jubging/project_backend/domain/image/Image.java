package mokindang.jubging.project_backend.domain.image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_board_id")
    private CertificationBoard certificationBoard;

    private String storeName;

    private String filePath;

    public Image(CertificationBoard certificationBoard, String storeName, String filePath) {
        this.certificationBoard = certificationBoard;
        this.storeName = storeName;
        this.filePath = filePath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Image image = (Image) o;
        return Objects.equals(id, image.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
