package pg.skilltest.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScoreServiceWithoutLambdaTest {

	@Autowired
	private ScoreService service;

	@Autowired
	private ScoreServiceWithoutLambda serviceWithoutLambda;

	@Test
	void getTotalScores() {
		List<String> actual = service.getTotalScores();
		List<String> lambdaResult = serviceWithoutLambda.getTotalScores();
		assertThat(actual).isEqualTo(lambdaResult);
	}

	@Test
	void getAverageScores() {
		List<String> actual = service.getAverageScores();
		List<String> lambdaResult = serviceWithoutLambda.getAverageScores();
		assertThat(actual).isEqualTo(lambdaResult);
	}

	@Test
	void getRanking() {
		List<String> actual = service.getRanking();
		List<String> lambdaResult = serviceWithoutLambda.getRanking();
		assertThat(actual).isEqualTo(lambdaResult);
	}

}
