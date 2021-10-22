package pg.skilltest.data;

import java.time.LocalTime;

import lombok.Data;

@Data
public class DepartureTime {

	/** オリジナルデータ */
	private String csvRecord;

	/** 分割した個別データ */
	private String weekday;
	private int hour;
	private int min;

	/** 変換済み時刻 */
	private LocalTime time;

	/** 有効なデータかどうかのフラグ（ヘッダは無効とみなす） */
	private boolean validData = true;

	/** インデックス */
	private final static int WEEKDAY_INDEX = 0;
	private final static int HOUR_INDEX = 1;
	private final static int MINUTE_INDEX = 2;

	/**
	 * CSVデータ1行分からコンストラクト
	 * CSV形式の文字列をバラして個別データとして登録する
	 */
	public DepartureTime(String csvRecord) {
		this.csvRecord = csvRecord;

		String[] ar = csvRecord.split(",");
		weekday = ar[WEEKDAY_INDEX];
		hour = toInt(ar[HOUR_INDEX]);
		min = toInt(ar[MINUTE_INDEX]);
		time = LocalTime.of(hour, min);
	}

	/**
	 * 平日データか週末データか
	 */
	private final static String WEEKDAY_FLAG = "1";
	public boolean isWeekdayData() {
		return WEEKDAY_FLAG.equals(weekday);
	}
	public boolean isWeekendData() {
		return !isWeekdayData();
	}

	/**
	 * 文字列から数値へ変換する。
	 * 変換不可の場合は 0 を返す。
	 */
	private int toInt(String s) {
		int ret = 0;
		try {
			ret = Integer.parseInt(s);
		} catch(NumberFormatException e) {
			validData = false;
		}
		return ret;
	}
}
