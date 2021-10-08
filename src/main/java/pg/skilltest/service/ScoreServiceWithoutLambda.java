package pg.skilltest.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pg.skilltest.data.Group;
import pg.skilltest.data.Student;
import pg.skilltest.helper.FileManager;

@Service
public class ScoreServiceWithoutLambda {

	@Autowired
	FileManager fileManager;

	/**
	 * 個人別合計点を取得する。
	 * 形式は Student#getTotalScoreString() による。
	 */
	public List<String> getTotalScores() {
		List<Student> studentList = getStudentList();
		List<String> resultList = new ArrayList<>();
		for(Student student : studentList) {
			resultList.add(student.getTotalScoreString());
		}
		return resultList;
	}

	/**
	 * 以下の情報を統合し取得する
	 * ・各グループの有効人数
	 * ・各グループの科目毎の平均点
	 * 形式は Group#getAverageScoreString() による。
	 */
	public List<String> getAverageScores() {
		Map<String, Group> groupMap = getGroupMap();
		List<Group> groupList = new ArrayList<>(groupMap.values());
		List<String> resultList = new ArrayList<>();
		for(Group group : groupList) {
			resultList.add(group.getAverageScoreString());
		}
		return resultList;
	}
	/**
	 * 合計点上位10名を取得する
	 * 形式は Student#getTotalScoreString() による。
	 */
	private final static int TOP_LIMIT = 10;
	public List<String> getRanking() {
		List<Student> studentList = getStudentList();
		Comparator<Student> comp = Comparator
			.comparing(Student::getTotalScore, Comparator.reverseOrder())
			.thenComparing(Student::getGroup)
			.thenComparing(Student::getNumber);
		studentList.sort(comp);

		List<String> resultList = new ArrayList<>();
		for(int ii = 0; ii < TOP_LIMIT; ii++) {
			resultList.add(studentList.get(ii).getTotalScoreString());
		}
		return resultList;
	}

	// ----- 以下 private メソッド -----

	/**
	 * 学生データのリストを作成する。
	 * ヘッダは含めない
	 */
	private List<Student> getStudentList() {
		List<String> csvLineList = fileManager.readScoreCsv();
		List<Student> resultList = new ArrayList<>();
		for(String line : csvLineList) {
			Student student = new Student(line);
			student.registEachData();
			if(student.isHeader()) { continue; }
			resultList.add(student);
		}
		return resultList;
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
