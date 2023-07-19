package mokindang.jubging.project_backend.recruitment_board.domain.vo.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Place {

    private Coordinate coordinate;

    private String address;

    public Place(final Coordinate coordinate, final String address) {
        this.coordinate = coordinate;
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Place place = (Place) o;

        if (!Objects.equals(coordinate, place.coordinate)) return false;
        return Objects.equals(address, place.address);
    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
