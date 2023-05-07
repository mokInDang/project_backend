package mokindang.jubging.project_backend.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mokindang.jubging.project_backend.member.domain.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_URL;
import static org.assertj.core.api.Assertions.assertThat;

class ProfileImageTest {

    @Test
    @DisplayName("ProfileImage 생성 시, 내부 필드 값은 DEFAULT 값으로 생성된다.")
    void createByDefaultValue(){
        //when
        ProfileImage profileImage = ProfileImage.createByDefaultValue();

        //then
        assertThat(profileImage.getProfileImageUrl()).isEqualTo(DEFAULT_PROFILE_IMAGE_URL);
    }

    @Test
    @DisplayName("ProfileImageUrl을 변경한다.")
    void updateProfileImage(){
        //given
        ProfileImage profileImage = ProfileImage.createByDefaultValue();

        //when
        profileImage.updateProfileImage("https://testimage.png");

        //then
        assertThat(profileImage.getProfileImageUrl()).isEqualTo("https://testimage.png");
    }
}
