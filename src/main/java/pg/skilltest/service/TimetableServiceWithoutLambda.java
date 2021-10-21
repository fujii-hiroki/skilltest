package pg.skilltest.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pg.skilltest.data.Timetable;
import pg.skilltest.helper.FileManager;

@Service
public class TimetableServiceWithoutLambda {

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
		List<Integer> hourList = new ArrayList<>(timetableMap.keySet());
		List<String> resultList = new ArrayList<>();
		for(int hour : hourList) {
			List<Timetable> timetableList = timetableMap.get(hour);
			List<String> minuteList = new ArrayList<>();
			for(Timetable tt : timetableList) {
				minuteList.add(String.valueOf(tt.getMin()));
			}
			resultList.add(hour + " | " + String.join(" ", minuteList));
		}
		return resultList;
	}

	/**
	 * 指定曜日・時間から直近3つの時間を取得する
	 */
	private final static int TOP_LIMIT = 3;
	public List<String> getRecent(String weekdayValue, LocalTime time) {
		List<Timetable> filteredList = getFilteredList(isWeekend(weekdayValue));

		// 時間の昇順
		filteredList.sort(Comparator.comparing(Timetable::getTime));

		List<String> resultList = new ArrayList<>();
		for(Timetable tt : filteredList) {
			if(isTimeEqualOrAfter(tt.getTime(), time)) {
				resultList.add(tt.getTime().toString());
			}
			if(resultList.size() == TOP_LIMIT) {
				break;
			}
		}

		if(resultList.size() > 0) { return resultList; }

		// 1つも取得できない場合は終電後の時間と見做し、始発からの時刻を取得
		for(int ii = 0; ii < TOP_LIMIT; ii++) {
			resultList.add(filteredList.get(ii).getTime().toString());
		}

		return resultList;
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
		List<Timetable> resultList = new ArrayList<>();
		for(Timetable tt : timetables) {
			if(isWeekend && tt.isWeekendData()) {
				resultList.add(tt);
			}
			else if(!isWeekend && tt.isWeekdayData()) {
				resultList.add(tt);
			}
		}
		return resultList;
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
		List<String> csvLineList = fileManager.readTimetableCsv();
		List<Timetable> resultList = new ArrayList<>();
		for(String line : csvLineList) {
			Timetable tt = new Timetable(line);
			if(tt.isValidData()) {
				resultList.add(tt);
			}
		}
		return resultList;
	}
}
