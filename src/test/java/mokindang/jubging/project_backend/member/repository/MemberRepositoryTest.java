package mokindang.jubging.project_backend.member.repository;

import mokindang.jubging.project_backend.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버의 email 과 alias를 데이터베이스에 저장한다.")
    public void save() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Member testMember = new Member("koho1047@naver.com", "고민호");

        //when
        Member saveMember = memberRepository.save(testMember);

        //then
        softly.assertThat(testMember).isEqualTo(saveMember);
        softly.assertThat(testMember.getId()).isEqualTo(saveMember.getId());
        softly.assertThat(testMember.getEmail()).isEqualTo(saveMember.getEmail());
        softly.assertThat(testMember.getAlias()).isEqualTo(saveMember.getAlias());
        softly.assertAll();
    }

    @Test
    @DisplayName("id가 주어지면 일치하는 멤버를 반환한다")
    public void findById() {
        //given
        Member testMember = new Member("koho1047@naver.com", "고민호");
        Member saveMember = memberRepository.save(testMember);

        //when
        Member findMember = memberRepository.findById(saveMember.getId())
                .get();

        //then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("email이 주어지면 일치하는 멤버를 반환한다.")
    public void findByEmail() {
        //given
        Member testMember = new Member("koho1047@naver.com", "고민호");
        Member saveMember = memberRepository.save(testMember);

        //when
        Member findMember = memberRepository.findByEmail(saveMember.getEmail())
                .get();

        //then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("존재하지 않는 email이 주어지면 아무것도 반환하지 않는다.")
    public void findByEmailError() {
        //given
        Member testMember = new Member("koho1047@naver.com", "고민호");
        Member saveMember = memberRepository.save(testMember);

        //when
        Optional<Member> findMember = memberRepository.findByEmail("abc12345@naver.com");

        //then
        assertThat(findMember).isEmpty();
    }

    @Test
    @DisplayName("모든 멤버를 반환한다.")
    public void findAll() {
        //given
        Member member1 = new Member("dog123@naver.com", "철수");
        Member member2 = new Member("cat456@naver.com", "영희");
        Member saveMember1 = memberRepository.save(member1);
        Member saveMember2 = memberRepository.save(member2);

        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members).contains(saveMember1, saveMember2);
    }
}
