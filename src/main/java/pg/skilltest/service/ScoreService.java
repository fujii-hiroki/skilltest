package pg.skilltest.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pg.skilltest.data.Group;
import pg.skilltest.data.Student;
import pg.skilltest.helper.FileManager;

@Service
public class ScoreService {

	@Autowired
	FileManager fileManager;

	/**
	 * 個人別合計点を取得する。
	 * 形式は Student#getTotalScoreString() による。
	 */
	public List<String> getTotalScores() {
		return getStudentList().stream()
		  .map(s -> s.getTotalScoreString())
			.collect(Collectors.toList());
	}

	/**
	 * 以下の情報を統合し取得する
	 * ・各グループの有効人数
	 * ・各グループの科目毎の平均点
	 * 形式は Group#getAverageScoreString() による。
	 */
	public List<String> getAverageScores() {
		// グループでソートしてから結果を取得する
		return getGroupMap().values().stream()
			.map(group -> group.getAverageScoreString())
			.collect(Collectors.toList());
	}
	/**
	 * 合計点上位10名を取得する
	 * 形式は Student#getTotalScoreString() による。
	 */
	private final static int TOP_LIMIT = 10;
	public List<String> getRanking() {
		return getStudentList().stream()
		  .sorted(Comparator
				.comparing(Student::getTotalScore).reversed()  // 点数の降順
				.thenComparing(Student::getGroup)  // グループ名の昇順
				.thenComparing(Student::getNumber)  // 番号の昇順
			)
			.limit(TOP_LIMIT)
		  .map(s -> s.getTotalScoreString())
			.collect(Collectors.toList());
	}

	// ----- 以下 private メソッド -----

	/**
	 * 学生データのリストを作成する。
	 * ヘッダは含めない
	 */
	private List<Student> getStudentList() {
		return fileManager.readScoreCsv().stream()
			.map(line -> {
				// 1行のデータからインスタンスを作成する
				Student student = new Student(line);
				student.registEachData();
				return student;
			})
			.filter(student -> !student.isHeader())  // ヘッダを除外
			.collect(Collectors.toList());
	}

	/**
	 * 学生リストからグループ毎のMapを作る
	 * Map<グループ名, グループ> 形式を返却する
	 */
	private Map<String, Group> getGroupMap() {
		// キー（グループ名）でソートしたいため、TreeMapを使用する
		Map<String, Group> groupMap = new TreeMap<>();

		List<Student> studentList = getStudentList();
		for(Student student : studentList) {
			String groupName = student.getGroup();
			if(!groupMap.containsKey(groupName)) {
				// 未作成の場合
				groupMap.put(groupName, new Group(groupName));
			}

			if(student.isValidData()) {
				// 有効データのみ格納する
				groupMap.get(groupName).addStudent(student);
			}
		}

		// 科目毎の点数を算出する
		groupMap.forEach((name, group) -> group.calculate());

		return groupMap;
	}
}
