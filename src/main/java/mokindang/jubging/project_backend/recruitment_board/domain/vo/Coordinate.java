package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

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

        if (!longitude.equals(that.longitude)) return false;
        return latitude.equals(that.latitude);
    }

    @Override
    public int hashCode() {
        int result = longitude.hashCode();
        result = 31 * result + latitude.hashCode();
        return result;
    }
}
