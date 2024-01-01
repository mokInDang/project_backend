package mokindang.jubging.project_backend.comment.service.strategy;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.comment.service.BoardType;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommentsSelectionStrategyFinder {

    private final Set<CommentsSelectionStrategy> commentsSelectionStrategies;

    public CommentsSelectionStrategy getCommentSelectionStrategy(final BoardType boardType) {
        return commentsSelectionStrategies.stream()
                .filter(strategy -> strategy.getBoardType() == boardType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 boardType 입니다."));
    }
}
