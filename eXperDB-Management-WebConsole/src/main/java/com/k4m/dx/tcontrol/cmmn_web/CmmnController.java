package com.k4m.dx.tcontrol.cmmn_web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
 
import com.k4m.dx.tcontrol.admin.accesshistory.service.AccessHistoryService;
import com.k4m.dx.tcontrol.admin.dbserverManager.service.DbServerVO;
import com.k4m.dx.tcontrol.backup.service.BackupService;
import com.k4m.dx.tcontrol.backup.service.WorkVO;
import com.k4m.dx.tcontrol.cmmn.AES256;
import com.k4m.dx.tcontrol.cmmn.AES256_KEY;
import com.k4m.dx.tcontrol.cmmn.CmmnUtils;
import com.k4m.dx.tcontrol.cmmn.client.ClientInfoCmmn;
import com.k4m.dx.tcontrol.cmmn.client.ClientProtocolID;
import com.k4m.dx.tcontrol.common.service.AgentInfoVO;
import com.k4m.dx.tcontrol.common.service.CmmnServerInfoService;
import com.k4m.dx.tcontrol.common.service.CmmnVO;
import com.k4m.dx.tcontrol.common.service.HistoryVO;
import com.k4m.dx.tcontrol.dashboard.service.DashboardService;
import com.k4m.dx.tcontrol.dashboard.service.DashboardVO;
import com.k4m.dx.tcontrol.encrypt.service.call.AgentMonitoringServiceCall;
import com.k4m.dx.tcontrol.encrypt.service.call.CommonServiceCall;
import com.k4m.dx.tcontrol.encrypt.service.call.StatisticsServiceCall;
import com.k4m.dx.tcontrol.functions.schedule.service.ScheduleService;

/**
 * 공통 컨트롤러 클래스를 정의한다.
 *
 * @author 변승우
 * @see
 * 
 *      <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2017.05.24   변승우 최초 생성
 *      </pre>
 */

@Controller
public class CmmnController {
	
	@Autowired
	private BackupService backupService;
	
	@Autowired
	private AccessHistoryService accessHistoryService;
	
	@Autowired
	private CmmnServerInfoService cmmnServerInfoService;
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private ScheduleService scheduleService;
	
	
	/**
	 * 메인(홈)을 보여준다.
	 * @return ModelAndView mv
	 */	
	@RequestMapping(value = "/experdb.do")
	public ModelAndView experdb(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request, ModelMap model) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("experdb/body");
		return mv;	
	}
	
	/**
	 * 데시보드화면을 보여준다.
	 * @return ModelAndView mv
	 */	
	@RequestMapping(value = "/dashboard.do")
	public ModelAndView dashboard(@ModelAttribute("historyVO") HistoryVO historyVO, HttpServletRequest request, ModelMap model) throws Exception {
	
		// 메인 이력 남기기
		CmmnUtils.saveHistory(request, historyVO);
		historyVO.setExe_dtl_cd("DX-T0004");
		accessHistoryService.insertHistory(historyVO);
		
		//스케줄 정보
		DashboardVO scheduleInfoVO = (DashboardVO) dashboardService.selectDashboardScheduleInfo();
		
		//백업정보
		DashboardVO backupInfoVO = (DashboardVO) dashboardService.selectDashboardBackupInfo();
		
		//데이터전송정보
		DashboardVO transferInfoVO = (DashboardVO) dashboardService.selectDashboardTransferInfoVO();
		
		DashboardVO vo = new DashboardVO();
		
		List<DashboardVO> serverInfoVO = (List<DashboardVO>) dashboardService.selectDashboardServerInfo(vo);
		
		Properties props = new Properties();
		props.load(new FileInputStream(ResourceUtils.getFile("classpath:egovframework/tcontrolProps/globals.properties")));
	
		String lang = props.get("lang").toString();
		
		ModelAndView mv = new ModelAndView();

		mv.addObject("scheduleInfo", scheduleInfoVO);
		mv.addObject("backupInfo", backupInfoVO);
		mv.addObject("serverInfo", serverInfoVO);
		mv.addObject("transferInfo", transferInfoVO);
		
		mv.setViewName("dashboard");
		return mv;	
	}
	

	
	/**
	 *  권한 에러 화면을 보여준다.
	 * 
	 * @param 
	 * @param request
	 * @return ModelAndView mv
	 * @throws Exception
	 */
	@RequestMapping(value = "/autError.do")
	public ModelAndView autError(HttpServletRequest request) {
				
		ModelAndView mv = new ModelAndView();
		try {	
				mv.setViewName("error/autError");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	
	/**
	 * DB서버에 대한 DB 리스트를 조회한다.
	 * 
	 * @return resultSet
	 * @throws Exception
	 */
	@RequestMapping(value = "/selectServerDBList.do")
	@ResponseBody
	public Map<String, Object> selectServerDBList (@ModelAttribute("dbServerVO") DbServerVO dbServerVO, HttpServletRequest request) {
		
		Map<String, Object> result =new HashMap<String, Object>();
	
		try {
			AES256 aes = new AES256(AES256_KEY.ENC_KEY);
			String db_svr_nm = request.getParameter("db_svr_nm");
			System.out.println(db_svr_nm);
			
			List<DbServerVO> resultSet = cmmnServerInfoService.selectDbServerList(db_svr_nm);
			
			JSONObject serverObj = new JSONObject();
			
			AgentInfoVO vo = new AgentInfoVO();
			vo.setIPADR(resultSet.get(0).getIpadr());
			
			AgentInfoVO agentInfo =  (AgentInfoVO) cmmnServerInfoService.selectAgentInfo(vo);
			
			String IP = resultSet.get(0).getIpadr();
			int PORT = agentInfo.getSOCKET_PORT();
			
			serverObj.put(ClientProtocolID.SERVER_NAME, resultSet.get(0).getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, resultSet.get(0).getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, resultSet.get(0).getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, resultSet.get(0).getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, resultSet.get(0).getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, aes.aesDecode(resultSet.get(0).getSvr_spr_scm_pwd()));
			
				
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.db_List(serverObj, IP, PORT);
	
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Tbale 리스트를 조회한다.
	 * @param WorkVO
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectTableList.do")
	@ResponseBody
	public Map<String, Object> selectTableList (@ModelAttribute("workVO") WorkVO workVO, HttpServletRequest request) {
		Map<String, Object> result =new HashMap<String, Object>();

		try {
			AES256 aes = new AES256(AES256_KEY.ENC_KEY);
			DbServerVO dbServerVO = backupService.selectDbSvrNm(workVO);
			JSONObject serverObj = new JSONObject();
			
			serverObj.put(ClientProtocolID.SERVER_NAME, dbServerVO.getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, dbServerVO.getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, dbServerVO.getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME, dbServerVO.getDft_db_nm());
			serverObj.put(ClientProtocolID.USER_ID, dbServerVO.getSvr_spr_usr_id());
			serverObj.put(ClientProtocolID.USER_PWD, aes.aesDecode(dbServerVO.getSvr_spr_scm_pwd()));
			
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.table_List(serverObj,  String.valueOf(workVO.getUsr_role_nm()));
			
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Object 리스트를 조회한다.
	 * @param WorkVO
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getObjectList.do")
	@ResponseBody
	public Map<String, Object> getObjectList (@ModelAttribute("workVO") WorkVO workVO, HttpServletRequest request) {
		Map<String, Object> result =new HashMap<String, Object>();

		try {
			AES256 aes = new AES256(AES256_KEY.ENC_KEY);
			DbServerVO dbServerVO = backupService.selectDbSvrNm(workVO);
			JSONObject serverObj = new JSONObject();
			
			AgentInfoVO vo = new AgentInfoVO();
			vo.setIPADR(dbServerVO.getIpadr());
			
			AgentInfoVO agentInfo =  (AgentInfoVO) cmmnServerInfoService.selectAgentInfo(vo);
			
			String IP = dbServerVO.getIpadr();
			int PORT = agentInfo.getSOCKET_PORT();
			
			String db_nm = request.getParameter("db_nm");
			
			serverObj.put(ClientProtocolID.SERVER_NAME, dbServerVO.getDb_svr_nm());
			serverObj.put(ClientProtocolID.SERVER_IP, dbServerVO.getIpadr());
			serverObj.put(ClientProtocolID.SERVER_PORT, dbServerVO.getPortno());
			serverObj.put(ClientProtocolID.DATABASE_NAME,  db_nm);
			serverObj.put(ClientProtocolID.USER_ID, dbServerVO.getSvr_spr_usr_id());
			//serverObj.put(ClientProtocolID.USER_PWD, dbServerVO.getSvr_spr_scm_pwd());
			serverObj.put(ClientProtocolID.USER_PWD, aes.aesDecode(dbServerVO.getSvr_spr_scm_pwd()));
			
			ClientInfoCmmn cic = new ClientInfoCmmn();
			result = cic.object_List(serverObj, IP, PORT);
			
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 디렉토리 존재유무 체크
	 * @param WorkVO
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/existDirCheck.do")
	@ResponseBody
	public Map<String, Object> existDirCheck (@ModelAttribute("workVO") WorkVO workVO, HttpServletRequest request, @ModelAttribute("dbServerVO") DbServerVO dbServerVO) {
		Map<String, Object> result =new HashMap<String, Object>();
		String directory_path = request.getParameter("path");
		List<DbServerVO> ipResult = null;		
		
		int db_svr_id = Integer.parseInt(request.getParameter("db_svr_id"));
		try {
			ipResult = (List<DbServerVO>) cmmnServerInfoService.selectAllIpadrList(db_svr_id);
			
			for(int i=0; i<ipResult.size(); i++){
				JSONObject serverObj = new JSONObject();
				
				AgentInfoVO vo = new AgentInfoVO();
				vo.setIPADR(ipResult.get(i).getIpadr());
				
				AgentInfoVO agentInfo =  (AgentInfoVO) cmmnServerInfoService.selectAgentInfo(vo);
				
				String IP = ipResult.get(i).getIpadr();
				int PORT = agentInfo.getSOCKET_PORT();
				
				serverObj.put(ClientProtocolID.SERVER_NAME, ipResult.get(i).getDb_svr_nm());
				serverObj.put(ClientProtocolID.SERVER_IP, ipResult.get(i).getIpadr());
				serverObj.put(ClientProtocolID.SERVER_PORT, ipResult.get(i).getPortno());

				ClientInfoCmmn cic = new ClientInfoCmmn();
				result = cic.directory_exist(serverObj,directory_path, IP, PORT);	
				

				int resultCd = Integer.parseInt(result.get("resultCode").toString());
				
				if(resultCd == 1){
					return result;
				}
				
			}	
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 스케줄 정보
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectScdInfo.do")
	@ResponseBody
	public List<Map<String, Object>> selectScdInfo(HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int scd_id = Integer.parseInt(request.getParameter("scd_id"));
			result = scheduleService.selectScdInfo(scd_id);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * WORK 정보
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectWrkInfo.do")
	@ResponseBody
	public List<Map<String, Object>> selectWrkInfo(HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int wrk_id = Integer.parseInt(request.getParameter("wrk_id"));
			
			result = scheduleService.selectWrkInfo(wrk_id);	
			System.out.println(result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	
	/**
	 * WORK OPTION 정보(DUMP)
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/workOptionLayer.do")
	@ResponseBody
	public List<Map<String, Object>> workOptionLayer(@ModelAttribute("workVO") WorkVO workVO, HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int bck_wrk_id = Integer.parseInt(request.getParameter("bck_wrk_id"));	
			result = backupService.selectWorkOptionLayer(bck_wrk_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	

	
	
	
	/**
	 * WORK Object 리스트 조회
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/workObjectListTreeLayer.do")
	@ResponseBody
	public List<Map<String, Object>> workObjectListTreeLayer(@ModelAttribute("workVO") WorkVO workVO, HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int bck_wrk_id = Integer.parseInt(request.getParameter("bck_wrk_id"));	
			result = backupService.selectWorkObjectLayer(bck_wrk_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	

	
	/**
	 * 아이피 정보
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectIpadrList.do")
	@ResponseBody
	public List<Map<String, Object>> selectIpadrList(HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int db_svr_id = Integer.parseInt(request.getParameter("db_svr_id"));			
			result = cmmnServerInfoService.selectIpadrList(db_svr_id);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	
	/**
	 * 작업로그정보
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectWrkErrorMsg.do")
	@ResponseBody
	public List<Map<String, Object>> selectWrkErrorMsg(HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int exe_sn = Integer.parseInt(request.getParameter("exe_sn"));			
			result = cmmnServerInfoService.selectWrkErrorMsg(exe_sn);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}		
	
	
	/**
	 * HA구성확인
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectHaCnt.do")
	@ResponseBody
	public List<Map<String, Object>> selectHaCnt(HttpServletRequest request) {
		List<Map<String, Object>> result = null;
		
		try {
			int db_svr_id = Integer.parseInt(request.getParameter("db_svr_id"));			
			result = cmmnServerInfoService.selectHaCnt(db_svr_id);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}		
	
		
	/**
	 * 데시보드 암호화통계
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/selectDashSecurityStatistics.do")	
	public @ResponseBody JSONObject selectDashSecurityStatistics(HttpServletRequest request) {
		
		JSONArray agentStatusList = new JSONArray();		
		JSONArray tResult = new JSONArray();		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> agentList = null;
		List<Map<String, Object>> agentStatusListResult = null;
		
		//통계결과
		JSONObject statisticsResult = new JSONObject();
		List<Map<String, Object>> statisticsListResult = null;
		
		HttpSession session = request.getSession();
		String restIp = (String)session.getAttribute("restIp");
		int restPort = (int)session.getAttribute("restPort");
		String strTocken = (String)session.getAttribute("tockenValue");
		String loginId = (String)session.getAttribute("usr_id");
		String entityId = (String)session.getAttribute("ectityUid");	

		try {								
			
			String from = request.getParameter("from");
			String to = request.getParameter("to");
			String categoryColumn = request.getParameter("categoryColumn");
			
			CommonServiceCall csc = new CommonServiceCall();			
			AgentMonitoringServiceCall amsc = new AgentMonitoringServiceCall();		
			
			agentList = csc.selectEntityList2(restIp, restPort, strTocken, loginId, entityId);			
			agentStatusList = amsc.selectSystemStatus(restIp, restPort, strTocken, loginId, entityId);
				
			agentStatusListResult = (List<Map<String, Object>>) agentStatusList;
		
			//암호화통계 결과
			StatisticsServiceCall ssc = new StatisticsServiceCall();
			statisticsResult = ssc.selectAuditLogSiteHourForStat(restIp, restPort, strTocken, loginId, entityId, from, to, categoryColumn);
			statisticsListResult= (List<Map<String, Object>>) statisticsResult.get("list");
			
			if(statisticsListResult.size() == 0){
				result.put("resultCode", statisticsResult.get("resultCode"));
				result.put("resultMessage", statisticsResult.get("resultMessage"));
			}else{
				for(int i=0; i<agentStatusListResult.size(); i++){	
					agentStatusListResult.get(i).put("encryptSuccessCount", "0");
					agentStatusListResult.get(i).put("encryptFailCount", "0");
					agentStatusListResult.get(i).put("decryptSuccessCount", "0");
					agentStatusListResult.get(i).put("decryptFailCount", "0");
					agentStatusListResult.get(i).put("sumCount", "0");
				
					tResult.add(agentStatusListResult.get(i));
				}
			
				if(statisticsListResult.get(0).get("categoryColumn").equals("-")){	
					for(int k=0; k<agentList.size(); k++){
						result.put("resultCode", agentList.get(0).get("resultCode"));
						result.put("resultMessage", agentList.get(0).get("resultMessage"));
						
						agentList.get(k).put("encryptSuccessCount", "0");
						agentList.get(k).put("encryptFailCount", "0");
						agentList.get(k).put("decryptSuccessCount", "0");
						agentList.get(k).put("decryptFailCount", "0");
						agentList.get(k).put("sumCount", "0");
						
						int temp =0;
						for(int i=0; i<agentStatusListResult.size(); i++){					
							if(agentList.get(k).get("createName").equals(agentStatusListResult.get(i).get("monitoredName"))){
								temp ++;
							}
						}
						if(temp == 0){
							JSONObject addList = new JSONObject();
							addList.put("monitoredName", agentList.get(k).get("createName"));
							addList.put("encryptSuccessCount", agentList.get(k).get("encryptSuccessCount"));
							addList.put("encryptFailCount", agentList.get(k).get("encryptFailCount"));
							addList.put("decryptSuccessCount", agentList.get(k).get("decryptSuccessCount"));
							addList.put("decryptFailCount", agentList.get(k).get("decryptFailCount"));
							addList.put("sumCount", agentList.get(k).get("sumCount"));
							addList.put("status", "start");
							tResult.add(addList);
						}
					}				
				}else{
					for(int k=0; k<agentList.size(); k++){
						int temp =0;
						result.put("resultCode", agentList.get(0).get("resultCode"));
						result.put("resultMessage", agentList.get(0).get("resultMessage"));
						for(int i =0; i<statisticsListResult.size(); i++){
							if(agentList.get(k).get("createName").toString().contains(statisticsListResult.get(i).get("categoryColumn").toString())){
								agentList.get(k).put("encryptSuccessCount", statisticsListResult.get(i).get("encryptSuccessCount"));
								agentList.get(k).put("encryptFailCount", statisticsListResult.get(i).get("encryptFailCount"));
								agentList.get(k).put("decryptSuccessCount", statisticsListResult.get(i).get("decryptSuccessCount"));
								agentList.get(k).put("decryptFailCount", statisticsListResult.get(i).get("decryptFailCount"));
								agentList.get(k).put("sumCount", statisticsListResult.get(i).get("sumCount"));
							}
						}						
						for(int i=0; i<agentStatusListResult.size(); i++){
							result.put("resultCode", agentStatusListResult.get(0).get("resultCode"));
							result.put("resultMessage", agentStatusListResult.get(0).get("resultMessage"));
							if(agentList.get(k).get("createName").equals(agentStatusListResult.get(i).get("monitoredName"))){
								temp ++;
							}
						}
						if(temp == 0){
							JSONObject addList = new JSONObject();
							addList.put("monitoredName", agentList.get(k).get("createName"));
							addList.put("encryptSuccessCount", agentList.get(k).get("encryptSuccessCount"));
							addList.put("encryptFailCount", agentList.get(k).get("encryptFailCount"));
							addList.put("decryptSuccessCount", agentList.get(k).get("decryptSuccessCount"));
							addList.put("decryptFailCount", agentList.get(k).get("decryptFailCount"));
							addList.put("sumCount", agentList.get(k).get("sumCount"));
							addList.put("status", "start");
							tResult.add(addList);
						}
					}
	
				}
				
			}
			
			/*for(int k=0; k<agentList.size(); k++){
				int temp =0;
				for(int i=0; i<agentStatusListResult.size(); i++){
					result.put("resultCode", agentStatusListResult.get(0).get("resultCode"));
					result.put("resultMessage", agentStatusListResult.get(0).get("resultMessage"));
					if(agentList.get(k).get("createName").equals(agentStatusListResult.get(i).get("monitoredName"))){
						temp ++;
					}
				}
				if(temp == 0){
					JSONObject addList = new JSONObject();
					addList.put("monitoredName", agentList.get(k).get("createName"));
					addList.put("status", "start");
					tResult.add(addList);
				}
			}*/
			
			result.put("list", tResult);
			System.out.println("결과="+ result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}		
	
	
	
	/**
	 * 데시보드 서버상태
	 * @param 
	 * @return resultSet
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/serverStatus.do")	
	public @ResponseBody JSONObject serverStatus(HttpServletRequest request) {
	
				JSONObject result = new JSONObject();

				HttpSession session = request.getSession();
				String restIp = (String)session.getAttribute("restIp");
				int restPort = (int)session.getAttribute("restPort");
				String strTocken = (String)session.getAttribute("tockenValue");
				String loginId = (String)session.getAttribute("usr_id");
				String entityId = (String)session.getAttribute("ectityUid");	

				try{
					CommonServiceCall csc = new CommonServiceCall();					
					result = csc.selectServerStatus(restIp, restPort, strTocken, loginId, entityId);
				}catch(Exception e){
					result.put("resultCode", "8000000002");
				}
		return result;
	}
}
