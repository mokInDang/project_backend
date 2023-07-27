package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class ParticipationCountTest {

    @Test
    @DisplayName("최대 인원수를 입력받아 객체를 생성한다. 생성 시 기본 참여인원은 1명이다.")
    void create() {
        //given
        int max = 8;

        //when, then
        assertThatCode(() -> ParticipationCount.createDefaultParticipationCount(max)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("최대 인원 수와 현재 인원을 반환한다. 생성 시 기본 참여인원은 1명이다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        int max = 8;

        //when
        ParticipationCount defaultParticipationCount = ParticipationCount.createDefaultParticipationCount(max);

        //then
        softly.assertThat(defaultParticipationCount.getCount()).isEqualTo(1);
        softly.assertThat(defaultParticipationCount.getMax()).isEqualTo(max);
    }

    @Test
    @DisplayName("참여 인원 수를 하나 올린다.")
    void countUp() {
        //given
        int max = 8;
        int expectedCount = 2;
        ParticipationCount defaultParticipationCount = ParticipationCount.createDefaultParticipationCount(max);

        //when
        defaultParticipationCount.countUp();

        //then
        assertThat(defaultParticipationCount.getCount()).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("인원 수를 올릴 때 최대인원수가 꽉 찬 경우 예외를 반환한다.")
    void validateCountUp() {
        //given
        int max = 2;
        ParticipationCount defaultParticipationCount = ParticipationCount.createDefaultParticipationCount(max);

        //when, then
        assertThatThrownBy(defaultParticipationCount::countUp).isInstanceOf(IllegalStateException.class)
                .hasMessage("참여 인원이 꽉 찼습니다.");
    }
}
