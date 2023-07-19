package mokindang.jubging.project_backend.recruitment_board.domain.vo.place;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Coordinate {

    @Embedded
    private Longitude longitude;

    @Embedded
    private Latitude latitude;

    public Coordinate(final Double longitude, final Double latitude) {
        this.longitude = new Longitude(longitude);
        this.latitude = new Latitude(latitude);
    }

    public Double getPointOfLongitude() {
        return longitude.getPoint();
    }

    public Double getPointOfLatitude() {
        return latitude.getPoint();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Coordinate that = (Coordinate) o;

        if (!Objects.equals(longitude, that.longitude)) return false;
        return Objects.equals(latitude, that.latitude);
    }

    @Override
    public int hashCode() {
        int result = longitude != null ? longitude.hashCode() : 0;
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        return result;
    }
}
