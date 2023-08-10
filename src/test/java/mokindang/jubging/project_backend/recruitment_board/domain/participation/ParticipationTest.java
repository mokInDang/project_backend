package mokindang.jubging.project_backend.recruitment_board.domain.participation;

import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipationTest {

    @ParameterizedTest
    @CsvSource(value = {"1, true", "2, false"})
    @DisplayName("입력받은 회원의 id 가 참여에 있는 id 와 일치하면 true 를 반환하고 일치하지 않을 경우 false 를 반환한다.")
    void isParticipatedIn(final Long memberId, final boolean expected) {
        //given
        RecruitmentBoard board = mock(RecruitmentBoard.class);
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        Participation participation = new Participation(board, member);

        //when
        boolean actual = participation.isParticipatedIn(memberId);

        //then
        assertThat(actual).isEqualTo(expected);
    }
}