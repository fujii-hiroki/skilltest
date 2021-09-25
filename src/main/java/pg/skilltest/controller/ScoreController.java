package pg.skilltest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pg.skilltest.service.ScoreService;

@Controller
public class ScoreController {

	@Autowired
	ScoreService service;

	@RequestMapping("/score")
	public String index() {
		return "score";
	}

	@RequestMapping("/score/total")
	public ModelAndView total(ModelAndView mv) {
		List<String> list = service.getTotalScores();
		mv.addObject("unsanitizedResult", join(list));

		mv.setViewName("score");
		return mv;
	}

	@RequestMapping("/score/average")
	public ModelAndView average(ModelAndView mv) {
		List<String> list = service.getAverageScores();
		mv.addObject("unsanitizedResult", join(list));

		mv.setViewName("score");
		return mv;
	}

	@RequestMapping("/score/ranking")
	public ModelAndView ranking(ModelAndView mv) {
		List<String> list = service.getRanking();
		mv.addObject("unsanitizedResult", join(list));

		mv.setViewName("score");
		return mv;
	}

	/**
	 * <br>で連結する
	 */
	private String join(List<String> list) {
		return String.join("<br>", list);
	}
}
