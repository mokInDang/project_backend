package mokindang.jubging.project_backend.comment.service;

import java.util.Arrays;

public enum BoardType {

    RECRUITMENT_BOARD("recruitment-board"), CERTIFICATION_BOARD("certification-board");

    private final String value;

    BoardType(final String value) {
        this.value = value;
    }

    public static BoardType from(final String boardTypeValue) {
        return Arrays.stream(values())
                .filter(boardType -> boardTypeValue.equals(boardType.value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 타입 입니다."));
    }
}
