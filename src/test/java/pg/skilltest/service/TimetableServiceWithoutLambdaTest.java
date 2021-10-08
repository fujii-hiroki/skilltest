package pg.skilltest.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class TimetableServiceWithoutLambdaTest {

	@Autowired
	private TimetableService service;

	@Autowired
	private TimetableServiceWithoutLambda serviceWithoutLambda;

	@Test
	void getCsvData() {
		List<String> actual = service.getCsvData();
		List<String> lambdaResult = serviceWithoutLambda.getCsvData();
		assertThat(actual).isEqualTo(lambdaResult);
	}

	@Test
	void getByDate() {
		// 日付でぐるぐる。
		// 処理内容的には1週間分で十分なのだがなんとなく1ヶ月分回しておく。
		for(int ii = 1; ii <= 31; ii++) {
			LocalDate d = LocalDate.of(2021, 10, ii);
			List<String> actual = service.getByDate(d);
			List<String> lambdaResult = serviceWithoutLambda.getByDate(d);
			assertThat(actual).isEqualTo(lambdaResult);
		}
	}

	@Test
	void getRecent() {
		// 平日or週末でぐるぐる。
		String[] weekdayValues = { "weekday", "weekend" };
		for(String weekdayVal : weekdayValues) {
			// 時間でぐるぐる。
			// 30分刻みとする。for文の値は恣意的。
			LocalTime time = LocalTime.of(0, 0, 0);
			for(int ii = 0; ii < 48; ii++) {
				time = time.plus(30, ChronoUnit.MINUTES);
				List<String> actual = service.getRecent(weekdayVal, time);
				List<String> lambdaResult = serviceWithoutLambda.getRecent(weekdayVal, time);
				assertThat(actual).isEqualTo(lambdaResult);
			}
		}
	}

}
