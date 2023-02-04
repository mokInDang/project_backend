package mokindang.jubging.project_backend.repository;

import mokindang.jubging.project_backend.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("멤버의 email 과 alias를 데이터베이스에 저장한다.")
    public void save(){
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
    public void findOne() {
        //given
        Member testMember = new Member("koho1047@naver.com", "고민호");
        Member saveMember = memberRepository.save(testMember);

        //when
        Member findMember = memberRepository.findOne(saveMember.getId());

        //then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("email이 주어지면 일치하는 멤버를 반환한다.")
    public void findOneByEmail() {
        //given
        Member testMember = new Member("koho1047@naver.com", "고민호");
        Member saveMember = memberRepository.save(testMember);

        //when
        Member findMember = memberRepository.findOneByEmail(saveMember.getEmail());

        //then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("alias가 주어지면 일치하는 멤버를 반환한다(이름 중복 없는 경우).")
    public void findByAlias() {
        //given
        Member member1 = new Member("abc123@naver.com", "고민호");
        Member saveMember1 = memberRepository.save(member1);

        //when
        List<Member> findMembers = memberRepository.findByAlias(saveMember1.getAlias());
        Member findMember = findMembers.get(0);
        String findAlias = findMember.getAlias();

        //then
        assertThat(findMember).isEqualTo(saveMember1);
        assertThat(findAlias).isEqualTo("고민호");
    }

    @Test
    @DisplayName("alias가 주어지면 일치하는 모든 멤버들을 반환한다.")
    public void findByAlias_중복_alias() {
        //given
        Member member1 = new Member("abc123@naver.com", "고민호");
        Member member2 = new Member("test789@naver.com", "고민호");
        Member saveMember1 = memberRepository.save(member1);
        Member saveMember2 = memberRepository.save(member2);


        //when
        List<Member> findMembers = memberRepository.findByAlias("고민호");

        //then
        assertThat(findMembers).contains(saveMember1,saveMember2);
    }

    @Test
    @DisplayName("모든 멤버를 반환한다.")
    public void findAll(){
        //given
        Member member1 = new Member("dog123@naver.com", "철수");
        Member member2 = new Member("cat456@naver.com", "영희");
        Member saveMember1 = memberRepository.save(member1);
        Member saveMember2 = memberRepository.save(member2);

        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members).contains(saveMember1,saveMember2);
    }

}