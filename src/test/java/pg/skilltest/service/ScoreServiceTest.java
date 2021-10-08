package pg.skilltest.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pg.skilltest.data.Student;

@SpringBootTest
public class ScoreServiceTest {

	@Autowired
	private ScoreService service;

	private Student createData() {
		return createData("A,1,鎌田 毅,36,66,99");
	}
	private Student createData(String csvRecord) {
		Student ret = new Student(csvRecord);
		ret.registEachData();
		return ret;
	}

  @Test
	void getTotalScores1() {
		List<String> actual = service.getTotalScores();

		assertThat(actual).hasSize(150);
		assertThat(actual.get(0)).isEqualTo(createData().getTotalScoreString());
	}
}
