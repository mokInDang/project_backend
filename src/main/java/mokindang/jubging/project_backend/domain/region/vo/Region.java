package mokindang.jubging.project_backend.domain.region.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Region {

    private static final String DEFAULT_REGION = "DEFAULT_REGION";

    @Column(name = "region")
    private String value;

    public static Region createByDefaultValue(){
        return new Region(DEFAULT_REGION);
    }

    private Region(String value) {
        this.value = value;
    }

    public void updateRegion(String value) {
        this.value = value;
    }

    public boolean isDefault() {
        return this.value.equals(DEFAULT_REGION);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Region region = (Region) o;

        return Objects.equals(value, region.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
