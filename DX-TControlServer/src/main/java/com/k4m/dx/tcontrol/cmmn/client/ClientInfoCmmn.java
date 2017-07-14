package com.k4m.dx.tcontrol.cmmn.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ClientInfoCmmn {

	String Ip = "222.110.153.162";
	int port = 9001;

	// 1. 서버 연결 테스트 (serverConn)
	public Map<String, Object> DbserverConn(JSONObject serverObj) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {

			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			objList = CA.dxT003(ClientTranCodeType.DxT003, serverObj);

			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);

			System.out.println("strDxExCode : " + " " + strDxExCode);
			System.out.println("strResultCode : " + " " + strResultCode);
			System.out.println("strErrCode : " + " " + strErrCode);
			System.out.println("strErrMsg : " + " " + strErrMsg);

			System.out.println("RESULT_DATA : " + " " + objList.get(ClientProtocolID.RESULT_DATA));

			result.put("result_data", objList.get(ClientProtocolID.RESULT_DATA));
			result.put("result_code", strResultCode);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 2. 데이터베이스 리스트 (dbList)
	public JSONObject db_List(JSONObject serverObj) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		List<Object> selectDBList = null;

		try {
			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			objList = CA.dxT001(ClientTranCodeType.DxT001, serverObj);

			String _tran_err_msg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);

			selectDBList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			System.out.println("strDxExCode : " + " " + strDxExCode);

			for (int i = 0; i < selectDBList.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				Object obj = selectDBList.get(i);
				HashMap hp = (HashMap) obj;
				String datname = (String) hp.get("datname");

				jsonObj.put("dft_db_nm", datname);
				jsonArray.add(jsonObj);
			}
			result.put("data", jsonArray);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 3. 테이블 리스트 (tableList)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject table_List(JSONObject serverObj, String strSchema) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		List<Object> selectList = null;

		try {
			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			objList = CA.dxT002(ClientTranCodeType.DxT002, serverObj, strSchema);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);

			selectList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			System.out.println("strDxExCode : " + " " + strDxExCode);
			System.out.println("resultCode : " + " " + (String) objList.get(ClientProtocolID.RESULT_CODE));

			for (int i = 0; i < selectList.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				Object obj = selectList.get(i);
				HashMap hp = (HashMap) obj;
				String table_schema = (String) hp.get("table_schema");
				String table_name = (String) hp.get("table_name");

				jsonObj.put("schema", table_schema);
				jsonObj.put("name", table_name);
				jsonArray.add(jsonObj);

				System.out.println(i + " " + table_schema + " " + table_name);
			}
			result.put("data", jsonArray);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 7. DB접근제어 C(dbAccess_create)
	public void dbAccess_create(JSONObject serverObj, JSONObject acObj) {
		try {
			JSONObject objList;
			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			JSONObject jObj = new JSONObject();
			jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT006);
			jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.COMMAND_CODE_C);
			jObj.put(ClientProtocolID.SERVER_INFO, serverObj);
			jObj.put(ClientProtocolID.ACCESS_CONTROL_INFO, acObj);

			objList = CA.dxT006(ClientTranCodeType.DxT006, jObj);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);
			System.out.println("RESULT_CODE : " + strResultCode);
			System.out.println("ERR_CODE : " + strErrCode);
			System.out.println("ERR_MSG : " + strErrMsg);

			CA.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 6. DB접근제어 R(dbAccessList-#주석처리 안되어있는것 set==0)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject dbAccess_select(JSONObject serverObj) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		try {
			JSONObject objList;

			JSONObject acObj = new JSONObject();
			acObj.put(ClientProtocolID.AC_SET, "1");
			acObj.put(ClientProtocolID.AC_TYPE, "host");
			acObj.put(ClientProtocolID.AC_DATABASE, "experdba");
			acObj.put(ClientProtocolID.AC_USER, "experdba");
			acObj.put(ClientProtocolID.AC_IP, "222.110.153.254");
			acObj.put(ClientProtocolID.AC_METHOD, "trust");
			acObj.put(ClientProtocolID.AC_OPTION, "");

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			JSONObject jObj = new JSONObject();
			jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT006);
			jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.COMMAND_CODE_R);
			jObj.put(ClientProtocolID.SERVER_INFO, serverObj);
			jObj.put(ClientProtocolID.ACCESS_CONTROL_INFO, acObj);

			objList = CA.dxT006(ClientTranCodeType.DxT006, jObj);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);

			System.out.println("RESULT_CODE : " + strResultCode);
			System.out.println("ERR_CODE : " + strErrCode);
			System.out.println("ERR_MSG : " + strErrMsg);

			List<Object> selectDBList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			for (int i = 1; i < selectDBList.size() - 1; i++) {
				JSONObject jsonObj = new JSONObject();
				Object obj = selectDBList.get(i);
				HashMap hp = (HashMap) obj;

				if (Integer.parseInt((String) hp.get("Set")) != 0) {
					String Seq = (String) hp.get("Seq");
					String Set = (String) hp.get("Set");
					String Type = (String) hp.get("Type");
					String Database = (String) hp.get("Database");
					String User = (String) hp.get("User");
					String Ipadr = (String) hp.get("Ip");
					String Method = (String) hp.get("Method");
					String Option = (String) hp.get("Option");

					jsonObj.put("Seq", Seq);
					jsonObj.put("Set", Set);
					jsonObj.put("Type", Type);
					jsonObj.put("Database", Database);
					jsonObj.put("User", User);
					jsonObj.put("Ipadr", Ipadr);
					jsonObj.put("Method", Method);
					jsonObj.put("Option", Option);

					jsonArray.add(jsonObj);
					System.out.println("seq : " + Seq + " Set : " + Set + " Type : " + Type + " Database : " + Database
							+ " User : " + User + " Ip : " + Ipadr + " Method : " + Method + " Option : " + Option);
				}

			}
			result.put("data", jsonArray);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 6. DB접근제어 R(dbAccessList-전체)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject dbAccess_selectAll(JSONObject serverObj) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		try {
			JSONObject objList;

			JSONObject acObj = new JSONObject();
			acObj.put(ClientProtocolID.AC_SET, "1");
			acObj.put(ClientProtocolID.AC_TYPE, "host");
			acObj.put(ClientProtocolID.AC_DATABASE, "experdba");
			acObj.put(ClientProtocolID.AC_USER, "experdba");
			acObj.put(ClientProtocolID.AC_IP, "222.110.153.254");
			acObj.put(ClientProtocolID.AC_METHOD, "trust");
			acObj.put(ClientProtocolID.AC_OPTION, "");

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			JSONObject jObj = new JSONObject();
			jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT006);
			jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.COMMAND_CODE_R);
			jObj.put(ClientProtocolID.SERVER_INFO, serverObj);
			jObj.put(ClientProtocolID.ACCESS_CONTROL_INFO, acObj);

			objList = CA.dxT006(ClientTranCodeType.DxT006, jObj);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);

			System.out.println("RESULT_CODE : " + strResultCode);
			System.out.println("ERR_CODE : " + strErrCode);
			System.out.println("ERR_MSG : " + strErrMsg);

			List<Object> selectDBList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			for (int i = 1; i < selectDBList.size() - 1; i++) {
				JSONObject jsonObj = new JSONObject();

				Object obj = selectDBList.get(i);
				HashMap hp = (HashMap) obj;
				String Seq = (String) hp.get("Seq");
				String Set = (String) hp.get("Set");
				String Type = (String) hp.get("Type");
				String Database = (String) hp.get("Database");
				String User = (String) hp.get("User");
				String Ipadr = (String) hp.get("Ip");
				String Method = (String) hp.get("Method");
				String Option = (String) hp.get("Option");

				jsonObj.put("Seq", Seq);
				jsonObj.put("Set", Set);
				jsonObj.put("Type", Type);
				jsonObj.put("Database", Database);
				jsonObj.put("User", User);
				jsonObj.put("Ipadr", Ipadr);
				jsonObj.put("Method", Method);
				jsonObj.put("Option", Option);

				jsonArray.add(jsonObj);

				System.out.println("seq : " + Seq + " Set : " + Set + " Type : " + Type + " Database : " + Database
						+ " User : " + User + " Ip : " + Ipadr + " Method : " + Method + " Option : " + Option);
			}
			result.put("data", jsonArray);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 6.DB접근제어 U(dbAccess_update)
	public void dbAccess_update(JSONObject serverObj,JSONObject acObj) {
		try {
			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			JSONObject jObj = new JSONObject();
			jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT006);
			jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.COMMAND_CODE_U);
			jObj.put(ClientProtocolID.SERVER_INFO, serverObj);
			jObj.put(ClientProtocolID.ACCESS_CONTROL_INFO, acObj);

			objList = CA.dxT006(ClientTranCodeType.DxT006, jObj);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);
			System.out.println("RESULT_CODE : " + strResultCode);
			System.out.println("ERR_CODE : " + strErrCode);
			System.out.println("ERR_MSG : " + strErrMsg);

			CA.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 6. DB접근제어 D(dbAccess_delete)
	public void dbAccess_delete(JSONObject serverObj, ArrayList arrSeq) {
		try {

			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			JSONObject jObj = new JSONObject();
			jObj.put(ClientProtocolID.DX_EX_CODE, ClientTranCodeType.DxT006);
			jObj.put(ClientProtocolID.COMMAND_CODE, ClientProtocolID.COMMAND_CODE_D);
			jObj.put(ClientProtocolID.SERVER_INFO, serverObj);
			jObj.put(ClientProtocolID.ARR_AC_SEQ, arrSeq);

			objList = CA.dxT006(ClientTranCodeType.DxT006, jObj);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strErrCode = (String) objList.get(ClientProtocolID.ERR_CODE);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);
			String strResultCode = (String) objList.get(ClientProtocolID.RESULT_CODE);
			System.out.println("RESULT_CODE : " + strResultCode);
			System.out.println("ERR_CODE : " + strErrCode);
			System.out.println("ERR_MSG : " + strErrMsg);

			CA.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 11. Role 리스트 (roleList)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject role_List(JSONObject serverObj) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		try {
			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			objList = CA.dxT011(ClientTranCodeType.DxT011, serverObj);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);

			List<Object> selectList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			System.out.println("strDxExCode : " + " " + strDxExCode);

			for (int i = 0; i < selectList.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				Object obj = selectList.get(i);

				HashMap hp = (HashMap) obj;

				jsonObj.put("rolname", (String) hp.get("rolname"));
				jsonArray.add(jsonObj);
				System.out.println(i + " " + (String) hp.get("rolname"));
			}
			CA.close();

			result.put("data", jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 12. 스키마 및 테이블 리스트 (objectList)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject object_List(JSONObject serverObj) {

		JSONArray jsonArray = new JSONArray(); // 객체를 담기위해 JSONArray 선언.
		JSONObject result = new JSONObject();

		List<Object> selectList = null;

		try {
			JSONObject objList;

			ClientAdapter CA = new ClientAdapter(Ip, port);
			CA.open();

			objList = CA.dxT012(ClientTranCodeType.DxT012, serverObj);

			String strErrMsg = (String) objList.get(ClientProtocolID.ERR_MSG);
			String strDxExCode = (String) objList.get(ClientProtocolID.DX_EX_CODE);

			selectList = (ArrayList<Object>) objList.get(ClientProtocolID.RESULT_DATA);

			System.out.println("strDxExCode : " + " " + strDxExCode);
			System.out.println("resultCode : " + " " + (String) objList.get(ClientProtocolID.RESULT_CODE));

			for (int i = 0; i < selectList.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				HashMap hp = (HashMap) selectList.get(i);

				jsonObj.put("schema", (String) hp.get("table_schema"));
				jsonObj.put("name", (String) hp.get("table_name"));
				jsonArray.add(jsonObj);

				System.out.println(i + " " + hp.get("table_schema") + " " + hp.get("table_name"));
			}
			result.put("data", jsonArray);

			CA.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
