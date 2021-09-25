package pg.skilltest.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FileManager {

	private final static String SCORE_CSV = "files/scores.csv";
	private final static String TIMETABLE_CSV = "files/timetable.csv";

	public List<String> readScoreCsv() {
		return readCsv(SCORE_CSV);
	}
	public List<String> readTimetableCsv() {
		return readCsv(TIMETABLE_CSV);
	}

	private List<String> readCsv(String fileName) {
		List<String> lines;
		try {
			String filePath = new ClassPathResource(fileName).getFile().getAbsolutePath();
			Path path = Paths.get(filePath);
			lines = Files.readAllLines(path);
		} catch (IOException e) {
			e.printStackTrace();
			lines = new ArrayList<>();
		}

		return lines;
	}
}
