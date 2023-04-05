package mokindang.jubging.project_backend.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.member.vo.ProfileImage;
import mokindang.jubging.project_backend.domain.member.vo.Region;

import javax.persistence.*;
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

    public void updateProfileImage(final String profileImageUrl, final String profileImageName) {
        this.profileImage.updateProfileImage(profileImageUrl, profileImageName);
    }

    public String getFourLengthEmail() {
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
