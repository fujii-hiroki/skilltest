package pg.skilltest.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pg.skilltest.service.TimetableService;

@Controller
public class TimetableController {

	@Autowired
	TimetableService service;

	@RequestMapping("/timetable")
	public String index() {
		return "timetable";
	}

	@RequestMapping("/timetable/csv")
	public ModelAndView csv(ModelAndView mv) {
		List<String> list = service.getCsvData();
		mv.addObject("unsanitizedResult", join(list));

		mv.setViewName("timetable");
		return mv;
	}

	@RequestMapping("/timetable/bydate")
	public ModelAndView bydate(
		ModelAndView mv,
		@RequestParam(name = "date", defaultValue = "") @DateTimeFormat(iso = ISO.DATE) LocalDate date
	) {
		if(date != null) {
			List<String> list = service.getByDate(date);
			mv.addObject("unsanitizedResult", join(list));
			mv.addObject("initialDate", date.toString());
		} else {
			mv.addObject("result", "不正なパラメータ");
		}

		mv.setViewName("timetable");
		return mv;
	}

	@RequestMapping("/timetable/recent")
	public ModelAndView ranking(
		ModelAndView mv,
		@RequestParam(name = "weekday", defaultValue = "") String weekdayValue,
		@RequestParam(name = "time", defaultValue = "") @DateTimeFormat(iso = ISO.TIME) LocalTime time
	) {
		if(time != null) {
			List<String> list = service.getRecent(weekdayValue, time);
			mv.addObject("unsanitizedResult", join(list));
			mv.addObject("initialWeekday", weekdayValue);
			mv.addObject("initialTime", time.toString());
		} else {
			mv.addObject("result", "不正なパラメータ");
		}

		mv.setViewName("timetable");
		return mv;
	}
	
	/**
	 * <br>で連結する
	 */
	private String join(List<String> list) {
		return String.join("<br>", list);
	}
}
