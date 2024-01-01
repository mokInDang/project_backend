package mokindang.jubging.project_backend.comment.service.strategy;

import mokindang.jubging.project_backend.comment.service.BoardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentsSelectionStrategyFinderTest {

    @Autowired
    private CommentsSelectionStrategyFinder commentsSelectionStrategyFinder;

    @ParameterizedTest
    @MethodSource(value = "boardTypeAndCommentSelectStrategyProvider")
    @DisplayName("입력받은 BoardType 을 처리할 수 있는 댓글 반환 전략을 반환한다.")
    void getCommentSelectionStrategy(final BoardType boardType, final Class<CommentsSelectionStrategy> expected) {
        //when
        CommentsSelectionStrategy commentSelectionStrategy = commentsSelectionStrategyFinder.getCommentSelectionStrategy(boardType);

        //then
        assertThat(commentSelectionStrategy.getClass()).isEqualTo(expected);
    }

    private static Stream<Arguments> boardTypeAndCommentSelectStrategyProvider() {
        return Stream.of(
                Arguments.of(BoardType.RECRUITMENT_BOARD, RecruitmentBoardCommentsSelectionStrategy.class),
                Arguments.arguments(BoardType.CERTIFICATION_BOARD, CertificationBoardCommentsSelectionStrategy.class)
        );
    }
}
