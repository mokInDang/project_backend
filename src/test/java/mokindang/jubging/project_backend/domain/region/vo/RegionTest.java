package mokindang.jubging.project_backend.domain.region.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class RegionTest {

    @Test
    @DisplayName("Region 생성 시, 디폴트 리즌으로 생성을 한다.")
    void createByDefaultRegion() {
        //given
        String expectRegion = "DEFAULT_REGION";

        //when
        Region region = Region.createByDefaultValue();

        //then
        assertThat(region.getValue()).isEqualTo(expectRegion);
    }

    @Test
    @DisplayName("리즌을 변경한다.")
    void updateRegion() {
        //given
        Region region = Region.createByDefaultValue();

        //when
        region.updateRegion("동작구");

        //then
        assertThat(region.getValue()).isEqualTo("동작구");
    }
}
