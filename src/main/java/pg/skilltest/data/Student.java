package pg.skilltest.data;

import lombok.Data;

@Data
public class Student {

	/** オリジナルデータ */
	private String csvRecord;

	/** 分割した個別データ */
	private String group;
	private int number;
	private String name;
	private int englishScore;
	private int mathScore;
	private int scienceScore;

	/** 有効なデータかどうかのフラグ */
	private boolean validData = true;

	/** 合計得点 */
	private int totalScore = 0;

	/** インデックス */
	private final static int GROUP_INDEX = 0;
	private final static int NUMBER_INDEX = 1;
	private final static int NAME_INDEX = 2;
	private final static int ENGLISH_SCORE_INDEX = 3;
	private final static int MATH_SCORE_INDEX = 4;
	private final static int SCIENCE_SCORE_INDEX = 5;

	/**
	 * CSVデータ1行分からコンストラクト
	 */
	public Student(String csvRecord) {
		this.csvRecord = csvRecord;
	}

	/**
	 * CSV形式の文字列をバラして個別データとして登録する
	 */
	public void registEachData() {
		String[] ar = csvRecord.split(",");
		group = ar[GROUP_INDEX];
		number = toInt(ar[NUMBER_INDEX]);
		name = ar[NAME_INDEX];

		String tmpScore = ar[ENGLISH_SCORE_INDEX];
		if(isNA(tmpScore)) {
			// NAデータの場合
			englishScore = Integer.MIN_VALUE;
			mathScore = Integer.MIN_VALUE;
			scienceScore = Integer.MIN_VALUE;
			validData = false;
		} else {
			englishScore = toInt(ar[ENGLISH_SCORE_INDEX]);
			mathScore = toInt(ar[MATH_SCORE_INDEX]);
			scienceScore = toInt(ar[SCIENCE_SCORE_INDEX]);

			totalScore = englishScore + mathScore + scienceScore;
		}
	}

	/**
	 * 文字列から数値へ変換する。
	 * 変換不可の場合は最小有効値を返す。
	 */
	private int toInt(String s) {
		int ret = Integer.MIN_VALUE;
		try {
			ret = Integer.parseInt(s);
		} catch(NumberFormatException e) {
			validData = false;
		}
		return ret;
	}

	/**
	 * NAデータかどうか
	 */
	private final static String NOT_APPLICAPABLE = "NA";
	private boolean isNA(String s) {
		return NOT_APPLICAPABLE.equals(s);
	}

	/**
	 * 合計得点表示用文字列を作成する
	 */
	public String getTotalScoreString() {
		StringBuffer sb = new StringBuffer();
		sb.append(group + "組 ");
		sb.append(String.format("%2d番 ", number));
		sb.append(String.format("%-10s ", name));
		sb.append(String.format("%3d点", totalScore));
		return sb.toString().replaceAll(" ", "&nbsp;");
	}

	/**
	 * ヘッダかどうか
	 */
	private final static String HEADER_PREFIX = "group,";
	public boolean isHeader() {
	  return csvRecord.startsWith(HEADER_PREFIX);
	}

}
