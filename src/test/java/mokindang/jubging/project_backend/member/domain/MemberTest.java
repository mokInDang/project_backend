package mokindang.jubging.project_backend.member.domain;

import mokindang.jubging.project_backend.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mokindang.jubging.project_backend.member.domain.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_URL;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("멤버 생성 시 멤버의 email 과 alias 를 반환하고 ProfileImageUrl은 DEFAULT 값을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Member member = new Member("cjh87467@gmail.com", "지환");

        //when
        String email = member.getEmail();
        String alias = member.getAlias();
        String profileImageUrl = member.getProfileImage().getProfileImageUrl();

        //then
        softly.assertThat(email).isEqualTo("cjh87467@gmail.com");
        softly.assertThat(alias).isEqualTo("지환");
        softly.assertThat(profileImageUrl).isEqualTo(DEFAULT_PROFILE_IMAGE_URL);
        softly.assertAll();
    }

    @Test
    @DisplayName("사용자의 지역을 변경한다.")
    void updateRegion() {
        //given
        Member member = new Member("test@mail.com", "test");

        //when
        member.updateRegion("동작구");

        //then
        assertThat(member.getRegion().getValue()).isEqualTo("동작구");
    }

    @Test
    @DisplayName("이메일의 앞자리 4개를 반환한다.")
    void getFourLengthEmail(){
        //given
        Member member = new Member("testfourlength123@mail.com", "test");

        //when
        String fourLengthEmail = member.getFirstFourDigitsOfWriterEmail();

        //then
        assertThat(fourLengthEmail).isEqualTo("test");
    }

    @Test
    @DisplayName("ProfileImageUrl을 변경한다.")
    void updateProfileImage(){
        //given
        Member member = new Member("test@mail.com", "test");

        //when
        member.updateProfileImage("http://testimage.png");

        //then
        assertThat(member.getProfileImage().getProfileImageUrl()).isEqualTo("http://testimage.png");
    }

    @Test
    @DisplayName("Member의 Alias를 변경한다.")
    void updateAlias(){
        //given
        Member member = new Member("test@mail.com", "test");

        //when
        member.updateAlias("newAlias");

        //then
        assertThat(member.getAlias()).isEqualTo("newAlias");
    }
}
