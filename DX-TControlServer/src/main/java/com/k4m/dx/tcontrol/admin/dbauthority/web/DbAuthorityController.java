package com.k4m.dx.tcontrol.admin.dbauthority.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.k4m.dx.tcontrol.common.service.CmmnHistoryService;
import com.k4m.dx.tcontrol.common.service.HistoryVO;

/**
 * DB권한 관리 컨트롤러 클래스를 정의한다.
 *
 * @author 김주영
 * @see
 * 
 *      <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2017.05.29   김주영 최초 생성
 *      </pre>
 */

@Controller
public class DbAuthorityController {

	@Autowired
	private CmmnHistoryService cmmnHistoryService;

	/**
	 * DB권한관리 화면을 보여준다.
	 * 
	 * @param historyVO
	 * @param request
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbAuthority.do")
	public ModelAndView dbAuthority(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			// DB권한관리 이력 남기기
			HttpSession session = request.getSession();
			String usr_id = (String) session.getAttribute("usr_id");
			String ip = (String) session.getAttribute("ip");
			historyVO.setUsr_id(usr_id);
			historyVO.setLgi_ipadr(ip);
			cmmnHistoryService.insertHistoryDbAuthority(historyVO);
			
			mv.setViewName("admin/dbAuthority/dbAuthority");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;

	}
}
