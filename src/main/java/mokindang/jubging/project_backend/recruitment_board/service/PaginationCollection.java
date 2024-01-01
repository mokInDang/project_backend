package mokindang.jubging.project_backend.recruitment_board.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationCollection<T> {

    private final List<T> itemsWithNextCursor;
    private final int countPerScroll;

    public static <T> PaginationCollection<T> of(List<T> itemsWithNextCursor, int size) {
        return new PaginationCollection<>(itemsWithNextCursor, size);
    }

    public List<T> getPagingItems() {
        return this.itemsWithNextCursor.subList(0, countPerScroll);
    }

    public boolean hasNext() {
        return this.itemsWithNextCursor.size() > countPerScroll;
    }

    public T getNextCursor() {
        int lastIndex = countPerScroll - 1;
        return itemsWithNextCursor.get(lastIndex);
    }
}
