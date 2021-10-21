package pg.skilltest.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pg.skilltest.data.Timetable;
import pg.skilltest.helper.FileManager;

@Service
public class TimetableService {

	@Autowired
	FileManager fileManager;

	/**
	 * CSVデータをそのまま取得する。
	 * 形式は Timetable#getCsvRecord() による。
	 */
	public List<String> getCsvData() {
		return fileManager.readTimetableCsv();
	}

	/**
	 * 指定された日付の時刻表を表示する
	 */
	public List<String> getByDate(LocalDate date) {
		Map<Integer, List<Timetable>> timetableMap = createTimetableMap(date);
		return timetableMap.keySet().stream()
		  .map(key -> {
				List<Timetable> list = timetableMap.get(key);
				List<String> minuteList = list.stream()
					.map(t -> String.valueOf(t.getMin()))
					.collect(Collectors.toList());
				return key + " | " + String.join(" ", minuteList);
			})
			.collect(Collectors.toList());
	}

	/**
	 * 指定曜日・時間から直近3つの時間を取得する
	 */
	private final static int TOP_LIMIT = 3;
	public List<String> getRecent(String weekdayValue, LocalTime time) {
		List<Timetable> filteredList = getFilteredList(isWeekend(weekdayValue));
		List<String> retList = filteredList.stream()
		  .sorted(Comparator.comparing(Timetable::getTime))  // 時間の昇順
			.filter(t -> {
				return isTimeEqualOrAfter(t.getTime(), time);
			})
			.limit(TOP_LIMIT)
		  .map(t -> t.getTime().toString())
			.collect(Collectors.toList());

		if(retList.size() > 0) { return retList; }

		// 1つも取得できない場合は終電後の時間と見做し、始発からの時刻を取得
		return filteredList.stream()
		  .sorted(Comparator.comparing(Timetable::getTime))
			.limit(TOP_LIMIT)
		  .map(t -> t.getTime().toString())
			.collect(Collectors.toList());
	}

	// ----- 以下 private メソッド -----

	/**
	 * 時刻表から時間でグルーピングしたMapを作る
	 * Map<時間(hour), 時刻> 形式を返却する
	 */
	private Map<Integer, List<Timetable>> createTimetableMap(LocalDate date) {
		// キー（時間）でソートしたいため、TreeMapを使用する
		Map<Integer, List<Timetable>> timetableMap = new TreeMap<>();

		for(Timetable timetable : getFilteredList(isWeekend(date))) {
			int hour = timetable.getHour();
			if(!timetableMap.containsKey(hour)) {
				// 未作成の場合
				timetableMap.put(hour, new ArrayList<>());
			}
			timetableMap.get(hour).add(timetable);
		}

		return timetableMap;
	}

	/**
	 * 平日のデータか週末のデータでフィルタ
	 */
	private List<Timetable> getFilteredList(boolean isWeekend) {
		List<Timetable> timetables = getTimetableList();
		return timetables.stream()
			.filter(t -> {
				return isWeekend? t.isWeekendData() : t.isWeekdayData();
			})
			.collect(Collectors.toList());
  }

	/**
	 * 週末かどうか
	 */
	private boolean isWeekend(LocalDate date) {
		return 
			date.getDayOfWeek().getValue() == DayOfWeek.SATURDAY.getValue() ||
			date.getDayOfWeek().getValue() == DayOfWeek.SUNDAY.getValue();
	}

	/**
	 * 週末かどうか
	 */
	private final static String WEEKEND_PARAM_VALUE = "weekend";
	private boolean isWeekend(String strValue) {
		return WEEKEND_PARAM_VALUE.equals(strValue);
	}

	/**
	 * t2がt1と同じまたは後であることを確認する
	 * （＝t2がt1より前ではないことを確認する）
	 */
	private boolean isTimeEqualOrAfter(LocalTime t1, LocalTime t2) {
		return !t1.isBefore(t2);
	}

	/**
	 * 時刻表リストを作成する。
	 * ヘッダは含めない
	 */
	private List<Timetable> getTimetableList() {
		return fileManager.readTimetableCsv().stream()
			.map(line -> {
				// 1行のデータからインスタンスを作成する
				return new Timetable(line);
			})
			.filter(table -> table.isValidData())  // ヘッダを除外
			.collect(Collectors.toList());
	}
}
