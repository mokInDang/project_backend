package mokindang.jubging.project_backend.domain.region.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
    @DisplayName("입력 받은 값을 기반으로 Region 객체를 생성한다.")
    void form() {
        //given
        String region = "동작구";

        //when
        Region actual = Region.from(region);

        //then
        assertThat(actual.getValue()).isEqualTo(region);
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

    @ParameterizedTest
    @MethodSource("regionParameterProvider")
    @DisplayName("DEFAULT_REGION 이면 true 를 반환하고, 그렇지 않은경우 false 를 반환한다.")
    void isDefaultRegion(final Region region, final boolean expect) {
        //when
        boolean actual = region.isDefault();

        //then
        assertThat(actual).isEqualTo(expect);
    }

    private static Stream<Arguments> regionParameterProvider() {
        Region noneDefaultRegion = Region.createByDefaultValue();
        noneDefaultRegion.updateRegion("동작구");
        return Stream.of(Arguments.of(Region.createByDefaultValue(), true),
                Arguments.of(noneDefaultRegion, false));
    }
}
