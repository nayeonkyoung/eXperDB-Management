package com.k4m.dx.tcontrol.accesscontrol.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.k4m.dx.tcontrol.accesscontrol.service.AccessControlService;
import com.k4m.dx.tcontrol.accesscontrol.service.AccessControlVO;
import com.k4m.dx.tcontrol.accesscontrol.service.DbIDbServerVO;
import com.k4m.dx.tcontrol.admin.accesshistory.service.AccessHistoryService;
import com.k4m.dx.tcontrol.cmmn.CmmnUtils;
import com.k4m.dx.tcontrol.cmmn.client.ClientInfoCmmn;
import com.k4m.dx.tcontrol.cmmn.client.ClientProtocolID;
import com.k4m.dx.tcontrol.common.service.HistoryVO;
import com.k4m.dx.tcontrol.functions.transfer.service.ConnectorVO;
import com.k4m.dx.tcontrol.functions.transfer.service.TransferService;

/**
 * 접근제어관리 컨트롤러 클래스를 정의한다.
 *
 * @author 김주영
 * @see
 * 
 *      <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2017.06.23   김주영 최초 생성
 *      </pre>
 */

@Controller
public class AccessControlController {
	
	@Autowired
	private AccessHistoryService accessHistoryService;
	
	@Autowired
	private TransferService transferService;
	
	@Autowired
	private AccessControlService accessControlService;
	
	
	/**
	 * 트리 Connector 리스트를 조회한다.
	 * 
	 * @return resultSet
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectTreeConnectorRegister.do")
	public @ResponseBody List<ConnectorVO> selectTreeConnectorRegister() {
		List<ConnectorVO> resultSet = null;
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			resultSet = transferService.selectConnectorRegister(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultSet;
	}
	
	
	/**
	 * 서버접근제어 화면을 보여준다.
	 * 
	 * @param 
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/accessControl.do")
	public ModelAndView serverAccessControl(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			// 접근제어관리 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0027");
			accessHistoryService.insertHistory(historyVO);
			
			//Database 목록 조회
			int db_svr_id = Integer.parseInt(request.getParameter("db_svr_id"));
			List<DbIDbServerVO> resultSet = accessControlService.selectDatabaseList(db_svr_id);
			if(resultSet.size()!=0){
				mv.addObject("db_svr_nm",resultSet.get(0).getDb_svr_nm());
				mv.addObject("resultSet",resultSet);
			}
			mv.setViewName("dbserver/accesscontrol");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	
	
	/**
	 * 접근제어 리스트를 조회한다.
	 * 
	 * @return resultSet
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectAccessControl.do")
	public @ResponseBody JSONObject selectAccessControl(HttpServletRequest request) {
		List<DbIDbServerVO> resultSet = null;
		JSONObject result = new JSONObject();
		try {
			int db_id = Integer.parseInt(request.getParameter("db_id"));
			resultSet = accessControlService.selectServerDb(db_id);
					
			JSONObject serverObj = new JSONObject();
			
			serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, resultSet.get(0).getSvr_spr_scm_pwd());
			
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.dbAccess_select(serverObj);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 접근제어 등록/수정 팝업을 보여준다.
	 * 
	 * @param request
	 * @param historyVO
	 * @throws Exception
	 */
	@RequestMapping(value = "/popup/accessControlRegForm.do")
	public ModelAndView connectorReg(@ModelAttribute("accessControlVO") AccessControlVO accessControlVO,HttpServletRequest request,@ModelAttribute("historyVO") HistoryVO historyVO) {
		ModelAndView mv = new ModelAndView();
		List<DbIDbServerVO> resultSet = null;
		Map<String, Object> result =new HashMap<String, Object>();
		try {
			String act = request.getParameter("act");
			CmmnUtils.saveHistory(request, historyVO);

			int db_id = Integer.parseInt(request.getParameter("db_id"));
			resultSet = accessControlService.selectServerDb(db_id);
			
			/*User 조회*/	
			JSONObject serverObj = new JSONObject();
								
			serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, resultSet.get(0).getSvr_spr_scm_pwd());

			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.role_List(serverObj);	
			if(act.equals("i")){
				//접근제어 등록 팝업 이력 남기기
				historyVO.setExe_dtl_cd("DX-T0028");
				accessHistoryService.insertHistory(historyVO);
								
			}
			if(act.equals("u")){
				//접근제어 수정 팝업 이력 남기기
				historyVO.setExe_dtl_cd("DX-T0028_01");
				accessHistoryService.insertHistory(historyVO);
				
				mv.addObject("prms_seq",request.getParameter("Seq").equals("undefined")?"":request.getParameter("Seq"));
				mv.addObject("prms_ipadr",request.getParameter("Ipadr").equals("undefined")?"":request.getParameter("Ipadr"));
				mv.addObject("prms_usr_id",request.getParameter("User").equals("undefined")?"":request.getParameter("User"));
				mv.addObject("ctf_mth_nm",request.getParameter("Method").equals("undefined")?"":request.getParameter("Method"));
				mv.addObject("ctf_tp_nm",request.getParameter("Type").equals("undefined")?"":request.getParameter("Type"));
				mv.addObject("opt_nm",request.getParameter("Option").equals("undefined")?"":request.getParameter("Option"));							
			}
			
			mv.addObject("result",result);
			mv.addObject("db_svr_nm",resultSet.get(0).getDb_svr_nm());
			mv.addObject("db_nm",resultSet.get(0).getDb_nm());
			mv.addObject("db_svr_id",resultSet.get(0).getDb_svr_id());
			mv.addObject("db_id",db_id);
			mv.addObject("act",act);
			mv.setViewName("popup/accessControlRegForm");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	
	/**
	 * 접근제어를 등록한다.
	 * 
	 * @param accessControlVO
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/insertAccessControl.do")
	public @ResponseBody void insertAccessControl(@ModelAttribute("accessControlVO") AccessControlVO accessControlVO,HttpServletRequest request,@ModelAttribute("historyVO") HistoryVO historyVO) {
		List<DbIDbServerVO> resultSet = null;
			try {		
				// 접근제어 등록 이력 남기기
				CmmnUtils.saveHistory(request, historyVO);
				historyVO.setExe_dtl_cd("DX-T0028_02");
				accessHistoryService.insertHistory(historyVO);
				
				resultSet = accessControlService.selectServerDb(accessControlVO.getDb_id());
				accessControlService.deleteDbAccessControl(accessControlVO.getDb_svr_id());
				
				JSONObject serverObj = new JSONObject();			
				serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
				serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
				serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
				serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
				serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
				serverObj.put(ClientProtocolID.USER_PWD, resultSet.get(0).getSvr_spr_scm_pwd());

				JSONObject acObj = new JSONObject();
				acObj.put(ClientProtocolID.AC_SET, "1");
				acObj.put(ClientProtocolID.AC_TYPE, accessControlVO.getCtf_tp_nm());
				acObj.put(ClientProtocolID.AC_DATABASE, resultSet.get(0).getDft_db_nm());
				acObj.put(ClientProtocolID.AC_USER, accessControlVO.getPrms_usr_id());
				acObj.put(ClientProtocolID.AC_IP, accessControlVO.getPrms_ipadr());
				acObj.put(ClientProtocolID.AC_METHOD, accessControlVO.getCtf_mth_nm());
				acObj.put(ClientProtocolID.AC_OPTION, accessControlVO.getOpt_nm());				
							
				ClientInfoCmmn cic = new ClientInfoCmmn();
				cic.dbAccess_create(serverObj, acObj);
				
				String id = (String) request.getSession().getAttribute("usr_id");	
				accessControlVO.setFrst_regr_id(id);
				accessControlVO.setLst_mdfr_id(id);		
				
				JSONObject result = cic.dbAccess_selectAll(serverObj);
				
				for (int i=0; i<result.size(); i++){
					JSONArray data = (JSONArray)result.get("data");
					for(int j=0;j<data.size(); j++){
						JSONObject jsonObj = (JSONObject)data.get(j);
							accessControlVO.setPrms_seq(Integer.parseInt((String)jsonObj.get("Seq")));
							accessControlVO.setPrms_set((String)jsonObj.get("Set"));
							accessControlVO.setPrms_ipadr((String)jsonObj.get("Ipadr"));
							accessControlVO.setPrms_usr_id((String)jsonObj.get("User"));
							accessControlVO.setCtf_mth_nm((String)jsonObj.get("Method"));
							accessControlVO.setCtf_tp_nm((String)jsonObj.get("Type"));
							accessControlVO.setOpt_nm((String)jsonObj.get("Option"));
							accessControlService.insertAccessControl(accessControlVO);
					}				
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	/**
	 * 접근제어를 수정한다.
	 * 
	 * @param accessControlVO
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateAccessControl.do")
	public @ResponseBody void updateAccessControl(@ModelAttribute("accessControlVO") AccessControlVO accessControlVO,HttpServletRequest request,@ModelAttribute("historyVO") HistoryVO historyVO) {
		List<DbIDbServerVO> resultSet = null;
		try {		
			// 접근제어 수정 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0028_03");
			accessHistoryService.insertHistory(historyVO);
			
			resultSet = accessControlService.selectServerDb(accessControlVO.getDb_id());
					
			JSONObject serverObj = new JSONObject();

			serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, resultSet.get(0).getSvr_spr_scm_pwd());
			
			JSONObject acObj = new JSONObject();
			acObj.put(ClientProtocolID.AC_SEQ, request.getParameter("prms_seq"));
			acObj.put(ClientProtocolID.AC_SET, "1");
			acObj.put(ClientProtocolID.AC_TYPE, accessControlVO.getCtf_tp_nm());
			acObj.put(ClientProtocolID.AC_DATABASE, resultSet.get(0).getDft_db_nm());
			acObj.put(ClientProtocolID.AC_USER, accessControlVO.getPrms_usr_id());
			acObj.put(ClientProtocolID.AC_IP, accessControlVO.getPrms_ipadr());
			acObj.put(ClientProtocolID.AC_METHOD, accessControlVO.getCtf_mth_nm());
			acObj.put(ClientProtocolID.AC_OPTION, accessControlVO.getOpt_nm());
			
			ClientInfoCmmn cic = new ClientInfoCmmn();
			cic.dbAccess_update(serverObj, acObj);
			
			accessControlService.deleteDbAccessControl(accessControlVO.getDb_svr_id());
			
			HttpSession session = request.getSession();
			String usr_id = (String)session.getAttribute("usr_id");
			accessControlVO.setPrms_set("1");
			accessControlVO.setFrst_regr_id(usr_id);
			accessControlVO.setLst_mdfr_id(usr_id);
			
			JSONObject result = cic.dbAccess_selectAll(serverObj);
			for (int i=0; i<result.size(); i++){
				JSONArray data = (JSONArray)result.get("data");
				for(int j=0;j<data.size(); j++){
					JSONObject jsonObj = (JSONObject)data.get(j);
						accessControlVO.setPrms_seq(Integer.parseInt((String)jsonObj.get("Seq")));
						accessControlVO.setPrms_set((String)jsonObj.get("Set"));
						accessControlVO.setPrms_ipadr((String)jsonObj.get("Ipadr"));
						accessControlVO.setPrms_usr_id((String)jsonObj.get("User"));
						accessControlVO.setCtf_mth_nm((String)jsonObj.get("Method"));
						accessControlVO.setCtf_tp_nm((String)jsonObj.get("Type"));
						accessControlVO.setOpt_nm((String)jsonObj.get("Option"));
						accessControlService.insertAccessControl(accessControlVO);
				}				
			}		
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 접근제어를 삭제한다.
	 * 
	 * @param accessControlVO
	 * @param request
	 * @param historyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAccessControl.do")
	public @ResponseBody boolean deleteAccessControl(@ModelAttribute("accessControlVO") AccessControlVO accessControlVO,HttpServletRequest request,@ModelAttribute("historyVO") HistoryVO historyVO) {
		List<DbIDbServerVO> resultSet = null;
		JSONObject serverObj = new JSONObject();
		ClientInfoCmmn cic = new ClientInfoCmmn();
			
		try {		
			// 접근제어 삭제 이력 남기기
			CmmnUtils.saveHistory(request, historyVO);
			historyVO.setExe_dtl_cd("DX-T0027_02");
			accessHistoryService.insertHistory(historyVO);
			
			int db_id = Integer.parseInt(request.getParameter("db_id"));
			
			accessControlVO.setDb_id(db_id);
			resultSet = accessControlService.selectServerDb(db_id);
						
			serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, resultSet.get(0).getSvr_spr_scm_pwd());
			
			String[] param = request.getParameter("rowList").toString().split(",");
			for (int i = 0; i < param.length; i++) {
				accessControlVO.setPrms_seq(Integer.parseInt(param[i]));

				//TODO 삭제 수정필요
				HashMap<String, String> hpSeq = new HashMap<String, String>();
				ArrayList arrSeq = new ArrayList();
				hpSeq.put(ClientProtocolID.AC_SEQ, param[i]);
				arrSeq.add(hpSeq);		
				System.out.println(param[i] + arrSeq + "삭제할거!");
				
				cic.dbAccess_delete(serverObj, arrSeq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

		
}
