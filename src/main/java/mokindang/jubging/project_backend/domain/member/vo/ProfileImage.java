package mokindang.jubging.project_backend.domain.member.vo;

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
    public static final String DEFAULT_PROFILE_IMAGE_NAME = "DEFAULT_PROFILE_IMAGE_NAME";

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_image_name")
    private String profileImageName;

    public ProfileImage(String profileImageUrl, String profileImageName) {
        this.profileImageUrl = profileImageUrl;
        this.profileImageName = profileImageName;
    }

    public static ProfileImage createByDefaultValue() {
        return new ProfileImage(DEFAULT_PROFILE_IMAGE_URL, DEFAULT_PROFILE_IMAGE_NAME);
    }

    public void updateProfileImage(final String profileImageUrl, final String profileImageName) {
        this.profileImageUrl = profileImageUrl;
        this.profileImageName = profileImageName;
    }
}
