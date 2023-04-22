package mokindang.jubging.project_backend.web.converter;

import mokindang.jubging.project_backend.comment.service.BoardType;
import org.springframework.core.convert.converter.Converter;

public class BoardTypeConverter implements Converter<String, BoardType> {

    @Override
    public BoardType convert(final String boardType) {
        return BoardType.from(boardType);
    }
}
