package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("모집여부가 true 인 게시글 중, 활동 시작일이 입력받은 날짜 보다 과거에 있는 경우 모집여부를 false 로 변경한다. ")
    void updateOnRecruitmentByStartingDate() {
        //given
        Member member = new Member("test@mail.com", "test");
        member.updateRegion("동작구");
        memberRepository.save(member);
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);
        Board recruitingBoardWithPastStartingDate = new Board(now, member, LocalDate.of(2023, 3, 27), "달리기", "제목", "본문");
        Board save = boardRepository.save(recruitingBoardWithPastStartingDate);

        LocalDate today = LocalDate.of(2023, 3, 28);

        //when
        boardRepository.updateOnRecruitmentByStartingDate(today);

        //then
        entityManager.clear();
        assertThat(boardRepository.findById(save.getId()).get().isOnRecruitment()).isFalse();
    }
}
