package pg.skilltest.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Group {

	/** グループ名 */
	private String group;

	/** 所属する学生 */
	private List<Student> studentList = new ArrayList<>();

	/** 所属学生の各科目総合点 */
	private double englishScore = 0;
	private double mathScore = 0;
	private double scienceScore = 0;

	/**
	 * コンストラクタ
	 */
	public Group(String group) {
		this.group = group;
	}

	/**
	 * 学生を追加する
	 */
	public void addStudent(Student student) {
		studentList.add(student);
	}

	/**
	 * 各科目の総合点を算出する
	 */
	public void calculate() {
		for(Student student : studentList) {
			englishScore += student.getEnglishScore();
			mathScore += student.getMathScore();
			scienceScore += student.getScienceScore();
		}
	}

	/**
	 * 平均点表示用文字列を作成する
	 */
	public String getAverageScoreString() {
		int num = studentList.size();
		StringBuffer sb = new StringBuffer();
		sb.append(group + "組");
		sb.append(String.format("（%d人） ", num));
		sb.append(String.format("英：%.1f ｜", englishScore / num));
		sb.append(String.format("数：%.1f ｜", mathScore / num));
		sb.append(String.format("理：%.1f", scienceScore / num));
		return sb.toString().replaceAll(" ", "&nbsp;");
	}
}
