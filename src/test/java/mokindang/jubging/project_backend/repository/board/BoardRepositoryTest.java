package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = new Member("test@mail.com", "test");
        member.updateRegion("동작구");
        memberRepository.save(member);
        LocalDate dateOfCreation = LocalDate.of(2023, 3, 15);

        LocalDate StartingDateOfRecruitingBoard = LocalDate.of(2023, 3, 19);
        Board recruitingBoard = new Board(member, StartingDateOfRecruitingBoard, "달리기", "제목", "본문", dateOfCreation);

        LocalDate pastStartingDate = LocalDate.of(2023, 3, 17);
        Board noneRecruitingBoard = new Board(member, pastStartingDate, "달리기", "제목", "본문", dateOfCreation);
        noneRecruitingBoard.closeRecruitment();

        Board recruitingBoardWithPastStartingDate = new Board(member, pastStartingDate, "달리기", "제목", "본문", dateOfCreation);

        boardRepository.save(recruitingBoard);
        boardRepository.save(noneRecruitingBoard);
        boardRepository.save(recruitingBoardWithPastStartingDate);
    }

    @Test
    @DisplayName("모집여부가 true 인 게시글 중, 활동 시작일이 입력받은 날짜 보다 과거에 있는 경우 모집여부를 false 로 변경한다. ")
    void updateOnRecruitmentByStartingDate() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDate today = LocalDate.of(2023, 3, 18);

        //when
        boardRepository.updateOnRecruitmentByStartingDate(today);

        //then
        softly.assertThat(boardRepository.findById(1L).get().isOnRecruitment()).isEqualTo(true);
        softly.assertThat(boardRepository.findById(2L).get().isOnRecruitment()).isEqualTo(false);
        softly.assertThat(boardRepository.findById(3L).get().isOnRecruitment()).isEqualTo(false);
    }
}
