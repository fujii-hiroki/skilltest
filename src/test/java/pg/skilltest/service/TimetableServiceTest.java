package pg.skilltest.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class TimetableServiceTest {

	@Autowired
	private TimetableService service;

	@Test
	void getCsvData1() {
		List<String> actual = service.getCsvData();

		assertThat(actual).hasSize(190);
		assertThat(actual.get(0)).isEqualTo("weekday,hour,min");
	}

}
