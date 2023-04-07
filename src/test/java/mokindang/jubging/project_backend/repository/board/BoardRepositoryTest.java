package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @DisplayName("모집여부가 true 인 게시글 중, 활동 시작일이 입력받은 날짜 보다 과거에 있는 경우 모집여부를 false 로 변경한다.")
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
        entityManager.flush();

        //then
        entityManager.clear();
        assertThat(boardRepository.findById(save.getId()).get().isOnRecruitment()).isFalse();
    }

    @Test
    @DisplayName("회원의 지역에 해당하는 게시글 리스트를 반환한다. 이때 게시글은 작성 일 자 기준 내림차순으로 정렬한다.")
    void selectRegionBoards() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);

        Member dongJackMember = new Member("test@mail.com", "동작이");
        dongJackMember.updateRegion("동작구");
        memberRepository.save(dongJackMember);
        Board dongJackBoard1 = new Board(now.plusDays(1), dongJackMember, LocalDate.of(2023, 3, 27), "달리기", "제목1", "본문1");
        Board dongJackBoard2 = new Board(now, dongJackMember, LocalDate.of(2023, 3, 27), "산책", "제목2", "본문2");
        boardRepository.save(dongJackBoard1);
        boardRepository.save(dongJackBoard2);

        Member sungDongMember = new Member("test2@maill.com", "성동이");
        sungDongMember.updateRegion("성동구");
        memberRepository.save(sungDongMember);
        Board sungDongBoard1 = new Board(now, sungDongMember, LocalDate.of(2023, 3, 27), "달리기", "제목1", "본문1");
        boardRepository.save(sungDongBoard1);

        Region targetRegion = Region.from("동작구");
        PageRequest input = PageRequest.of(0, 2);

        //when
        Slice<Board> boards = boardRepository.selectRegionBoards(targetRegion, input);

        //then
        assertThat(boards.getContent()).isEqualTo(List.of(dongJackBoard1, dongJackBoard2));
    }
}
