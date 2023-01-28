package mokindang.jubging.project_backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BTest {

    @Test
    @DisplayName("")
    void test1() {
        //given
        A a = new A("123", "123432");

        //when
        System.out.println(a.a);
        System.out.println(a.b);

        //then
    }
}
