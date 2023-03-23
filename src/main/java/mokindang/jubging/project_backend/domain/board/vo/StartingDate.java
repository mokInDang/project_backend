package mokindang.jubging.project_backend.domain.board.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StartingDate {

    @Column(name = "starting_date", nullable = false)
    private LocalDate startingDate;

    public StartingDate(final LocalDate currentDate, final LocalDate startingDate) {
        validateDate(currentDate, startingDate);
        this.startingDate = startingDate;
    }

    private void validateDate(final LocalDate currentDate, final LocalDate startingDate) {
        if (currentDate.isAfter(startingDate)) {
            throw new IllegalArgumentException("이미 지난 날짜는 활동 시작일로 할 수 없습니다.");
        }
    }

    public boolean isBefore(final LocalDate now) {
        return startingDate.isBefore(now);
    }

    public String getValue() {
        return this.startingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StartingDate that = (StartingDate) o;

        return Objects.equals(startingDate, that.startingDate);
    }

    @Override
    public int hashCode() {
        return startingDate != null ? startingDate.hashCode() : 0;
    }
}
