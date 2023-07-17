package mokindang.jubging.project_backend.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.member.domain.vo.ProfileImage;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.recruitment_board.domain.Participation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String alias;

    @Embedded
    private Region region;

    @Embedded
    private ProfileImage profileImage;

    public Member(final String email, final String alias) {
        this.email = email;
        this.alias = alias;
        region = Region.createByDefaultValue();
        profileImage = ProfileImage.createByDefaultValue();
    }

    public void updateRegion(final String region) {
        this.region.updateRegion(region);
    }

    public void updateProfileImage(final String profileImageUrl) {
        this.profileImage.updateProfileImage(profileImageUrl);
    }

    public void updateAlias(final String alias){
        this.alias = alias;
    }

    public String getFirstFourDigitsOfWriterEmail() {
        return this.email.substring(0, 4);
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
