package mokindang.jubging.project_backend.domain.member;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    @DisplayName("멤버의 email 과 alias 를 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Member member = new Member("cjh87467@gmail.com", "지환");

        //when
        String email = member.getEmail();
        String alias = member.getAlias();

        //then
        softly.assertThat(email).isEqualTo("cjh87467@gmail.com");
        softly.assertThat(alias).isEqualTo("지환");
        softly.assertAll();
    }
}
