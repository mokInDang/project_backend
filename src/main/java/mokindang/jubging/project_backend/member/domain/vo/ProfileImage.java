package mokindang.jubging.project_backend.member.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileImage {

    public static final String DEFAULT_PROFILE_IMAGE_URL = "DEFAULT_PROFILE_IMAGE_URL";

    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl;

    public ProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static ProfileImage createByDefaultValue() {
        return new ProfileImage(DEFAULT_PROFILE_IMAGE_URL);
    }

    public void updateProfileImage(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
