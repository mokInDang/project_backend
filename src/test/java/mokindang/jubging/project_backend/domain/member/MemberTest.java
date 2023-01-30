package mokindang.jubging.project_backend.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {

    @Test
    @DisplayName("멤버는 email과 alias를 반환한다.")
    public void getter() {
        //given
        Member member1 = new Member("koho1047@naver.com", "민호");

        //when
        String email = member1.getEmail();
        String alias = member1.getAlias();

        //then
        assertThat(email).isEqualTo("koho1047@naver.com");
        assertThat(alias).isEqualTo("민호");
    }

}