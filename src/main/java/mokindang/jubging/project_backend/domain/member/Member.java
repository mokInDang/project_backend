package mokindang.jubging.project_backend.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.region.vo.Region;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String alias;

    @Embedded
    private Region region;

    public Member(final String email, final String alias) {
        this.email = email;
        this.alias = alias;
        region = Region.createByDefaultValue();
    }

    public void updateRegion(final String region) {
        this.region.updateRegion(region);
    }

    public void updateRegion(final String region) {
        this.region = new Region(region);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
