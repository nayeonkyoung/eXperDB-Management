<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	/**
	* @Class Name : dumpRegReForm.jsp
	* @Description : rman 백업 수정 화면
	* @Modification Information
	*
	*   수정일         수정자                   수정내용
	*  ------------    -----------    ---------------------------
	*  2017.06.07     최초 생성
	*
	* author YoonJH
	* since 2017.06.07
	*
	*/
%>
<!doctype html>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>eXperDB</title>
<link rel="stylesheet" type="text/css" href="/css/common.css">
<script type="text/javascript" src="/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript">
// 저장후 작업ID
var wrk_id = null;

/* ********************************************************
 * DB Object initialization
 ******************************************************** */
var workObj = {"obj":[
	<c:forEach var="result" items="${workObjList}" varStatus="status">
	{"scm_nm":"${result.scm_nm}","obj_nm":"${result.obj_nm}"},
	</c:forEach>
	{"scm_nm":"","obj_nm":""}
]};

/* ********************************************************
 * Checkbox, Object List initialization
 ******************************************************** */
$(window.document).ready(
	function() {
		fn_get_object_list("${workInfo[0].db_id}","${workInfo[0].db_nm}");
		checkSection();
		changeFileFmtCd();
		checkOid();
});

/* ********************************************************
 * Dump Backup Update
 ******************************************************** */
function fn_update_work(){
	if(valCheck()){
		$.ajax({
			async : false,
			url : "/popup/workDumpReWrite.do",
		  	data : {
		  		wrk_id : $("#wrk_id").val(),
		  		wrk_nm : $("#wrk_nm").val(),
		  		wrk_exp : $("#wrk_exp").val(),
		  		db_id : $("#db_id").val(),
		  		bck_bsn_dscd : "TC000202",
		  		save_pth : $("#save_pth").val(),
		  		file_fmt_cd : $("#file_fmt_cd").val(),
		  		cprt : $("#cprt").val(),
		  		encd_mth_nm : $("#encd_mth_nm").val(),
		  		usr_role_nm : $("#usr_role_nm").val(),
		  		file_stg_dcnt : $("#file_stg_dcnt").val(),
		  		bck_mtn_ecnt : $("#bck_mtn_ecnt").val()
		  	},
			type : "post",
			error : function(request, xhr, status, error) {
				alert("실패");
			},
			success : function(data) {
				if($.trim(data) == "S"){
					fn_insert_opt();
				}else{
					alert("저장중에 에러가 발생했습니다. 다시 시도해 주세요.");
				}
			}
		});  
	}
}

/* ********************************************************
 * Dump Backup Option Insert
 ******************************************************** */
function fn_insert_opt(){
	var sn = 1;
	$("input[name=opt]").each(function(){
		if( $(this).not(":disabled") && $(this).is(":checked")){
			fn_insert_opt_val($("#wrk_id").val(),sn,$(this).attr("grp_cd"),$(this).attr("opt_cd"),"Y");
		}
		sn++;
	});

	fn_insert_object();
}

/* ********************************************************
 * Dump Backup Each Option Insert
 ******************************************************** */
function fn_insert_opt_val(wrk_id, opt_sn, grp_cd, opt_cd, bck_opt_val){
	$.ajax({
		async : false,
		url : "/popup/workOptWrite.do",
	  	data : {
	  		wrk_id : wrk_id,
	  		opt_sn : opt_sn,
	  		grp_cd : grp_cd,
	  		opt_cd : opt_cd,
	  		bck_opt_val : bck_opt_val
	  	},
		type : "post",
		error : function(request, xhr, status, error) {},
		success : function() {}
	});
}

/* ********************************************************
 * Dump Backup Object Insert
 ******************************************************** */
function fn_insert_object(){
	$("input[name=tree]").each(function(){
		if( $(this).is(":checked")){
			fn_insert_object_val($("#wrk_id").val(),$(this).attr("otype"),$(this).attr("schema"),$(this).val());
		}
	});

	opener.fn_dump_find_list();
	alert("수정등록이 완료되었습니다.");
	self.close();
}

/* ********************************************************
 * Dump Backup Each Object Insert
 ******************************************************** */
function fn_insert_object_val(wrk_id,otype,scm_nm,obj_nm){
	var db_id = $("#db_id").val();

	if(otype != "table") obj_nm = "";
	$.ajax({
		async : false,
		url : "/popup/workObjWrite.do",
	  	data : {
	  		wrk_id : wrk_id,
	  		db_id : db_id,
	  		scm_nm : scm_nm,
	  		obj_nm : obj_nm
	  	},
		type : "post",
		error : function(request, xhr, status, error) {},
		success : function() {}
	});
}

/* ********************************************************
 * Validation Check
 ******************************************************** */
function valCheck(){
	if($("#db_id option:selected" ).val() == ""){
		alert("백업할 Database를 선택하세요.");
		return false;
	}
	if($("#wrk_nm").val() == ""){
		alert("Work명을 입력해 주세요.");
		$("#wrk_nm").focus();
		return false;
	}
	if($("#wrk_exp").val() == ""){
		alert("Work설명을 입력해 주세요.");
		$("#wrk_exp").focus();
		return false;
	}
	if($("#save_pth").val() == ""){
		alert("저장경로를 입력해 주세요.");
		$("#save_pth").focus();
		return false;
	}
	
	return true;
}

/* ********************************************************
 * Get Selected Database`s Object List
 ******************************************************** */
function fn_get_object_list(in_db_id,in_db_nm){
	var db_nm = in_db_nm;
	var db_id = in_db_id;
	
	if(in_db_id == "" || in_db_nm == ""){
		db_nm = $( "#db_id option:selected" ).text();
		db_id = $( "#db_id option:selected" ).val();
	}

	if(db_nm != "" && db_id != ""){
		$.ajax({
			async : false,
			url : "/getObjectList.do",
		  	data : {
		  		db_svr_id : $("#db_svr_id").val(),
		  		db_nm : db_nm
		  	},
			type : "post",
			error : function(request, xhr, status, error) {
				alert("실패");
			},
			success : function(data) {
				fn_make_object_list(data);
			}
		});
	}else{
		$(".tNav").html("");
	}
}

/* ********************************************************
 * Make Object Tree
 ******************************************************** */
function fn_make_object_list(data){
	var html = "<ul>";
	var schema = "";
	var schemaCnt = 0;
	$(data.data).each(function (index, item) {
		var inSchema = item.schema;
		
		if(schemaCnt > 0 && schema != inSchema){
			html += "</ul></li>\n";
		}
		if(schema != inSchema){
			var checkStr = "";
			$(workObj.obj).each(function(i,v){
				if(v.scm_nm == item.schema && v.obj_nm == "") checkStr = " checked";
			});
			html += "<li class='active'><a href='#'>"+item.schema+"</a>";
			html += "<div class='inp_chk'>";
			html += "<input type='checkbox' id='schema"+schemaCnt+"' name='tree' value='"+item.schema+"' otype='schema' schema='"+item.schema+"'"+checkStr+"/><label for='schema"+schemaCnt+"'></label>";
			html += "</div>";
			html += "<ul>\n";
		}
		
		var checkStr = "";
		$(workObj.obj).each(function(i,v){
			if(v.scm_nm == item.schema && v.obj_nm == item.name) checkStr = " checked";
		});
		html += "<li><a href='#'>"+item.name+"</a>";
		html += "<div class='inp_chk'>";
		html += "<input type='checkbox' id='table"+index+"' name='tree' value='"+item.name+"' otype='table' schema='"+item.schema+"'"+checkStr+"/><label for='table"+index+"'></label>";
		html += "</div>";
		html += "</li>\n";

		if(schema != inSchema){
			schema = inSchema;
			schemaCnt++;
		}
	});
	if(schemaCnt > 0) html += "</ul></li>";
	html += "</ul>";

	$(".tNav").html("");
	$(".tNav").html(html);
	$.getScript( "/js/common.js", function() {});
}

/* ********************************************************
 * File Format에 따른 Checkbox disabled Check
 ******************************************************** */
function changeFileFmtCd(){
	if($("#file_fmt_cd").val() == "TC000401"){
		$("#cprt").removeAttr("disabled");
	}else{
		$("#cprt").attr("disabled",true);
	}
	
	if($("#file_fmt_cd").val() == "TC000402"){
		$("input[name=opt]").each(function(){
			if( $(this).attr("opt_cd") == "TC000801" || $(this).attr("opt_cd") == "TC000903" || $(this).attr("opt_cd") == "TC000904" ){
				$(this).removeAttr("disabled");
			}
		});
	}else{
		$("input[name=opt]").each(function(){
			if( $(this).attr("opt_cd") == "TC000801" || $(this).attr("opt_cd") == "TC000903" || $(this).attr("opt_cd") == "TC000904" ){
				$(this).attr("disabled",true);
			}
		});
	}
}

/* ********************************************************
 * Sections에 체크시 Object형태중 Only data, Only Schema를 비활성화 시킨다.
 ******************************************************** */
function checkSection(){
	var check = false;
	$("input[name=opt]").each(function(){
		if( ($(this).attr("opt_cd") == "TC000601" || $(this).attr("opt_cd") == "TC000602" || $(this).attr("opt_cd") == "TC000603") && $(this).is(":checked")){
			check = true;
		}
	});
	
	$("input[name=opt]").each(function(){
		if( $(this).attr("opt_cd") == "TC000701" || $(this).attr("opt_cd") == "TC000702" ){
			if(check){
				$(this).attr("disabled",true);
			}else{
				$(this).removeAttr("disabled");
			}
		}
	});
}

/* ********************************************************
 * Object형태 중 Only data, Only Schema 중 1개만 체크가능
 ******************************************************** */
function checkObject(code){
	var check1 = false;
	var check2 = false;

	$("input[name=opt]").each(function(){
		if(code == "TC000701" && $(this).attr("opt_cd") == "TC000701" && $(this).is(":checked") ){
			check1 = true;
		}else if(code == "TC000702" && $(this).attr("opt_cd") == "TC000702" && $(this).is(":checked") ){
			check2 = true;
		}
	});
	
	$("input[name=opt]").each(function(){
		if(check1 && code == "TC000701" && $(this).attr("opt_cd") == "TC000702"){
			$(this).attr('checked', false);
		}else if(check2 && code == "TC000702" && $(this).attr("opt_cd") == "TC000701"){
			$(this).attr('checked', false);
		}
	});
}

/* ********************************************************
 * 쿼리에서 Use Column Inserts, Use Insert Commands선택시 "OIDS포함" disabled
 ******************************************************** */
function checkOid(){
	var check = false;
	$("input[name=opt]").each(function(){
		if( ($(this).attr("opt_cd") == "TC000901" || $(this).attr("opt_cd") == "TC000902") && $(this).is(":checked")){
			check = true;
		}
	});
	
	$("input[name=opt]").each(function(){
		if( $(this).attr("opt_cd") == "TC001001" ){
			if(check){
				$(this).attr("disabled",true);
			}else{
				$(this).removeAttr("disabled");
			}
		}
	});
}
</script>
</head>
<body>
<div class="pop_container">
	<div class="pop_cts">
		<p class="tit">Dump 백업 수정하기</p>
		<div class="pop_cmm">
			<form name="workRegForm">
			<input type="hidden" name="db_svr_id" id="db_svr_id" value="${db_svr_id}"/>
			<input type="hidden" name="wrk_id" id="wrk_id" value="${wrk_id}"/> 
			<table class="write">
				<caption>Dump 백업 수정하기</caption>
				<colgroup>
					<col style="width:95px;" />
					<col />
					<col style="width:95px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th scope="row" class="ico_t1">Work명</th>
						<td><input type="text" class="txt" name="wrk_nm" id="wrk_nm" maxlength=50 value="<c:out value="${workInfo[0].wrk_nm}"/>"/></td>
						<th scope="row" class="ico_t1">Database</th>
						<td>
							<select name="db_id" id="db_id" class="select"  onChange="fn_get_object_list('','');">
								<option value="">선택</option>
								<c:forEach var="result" items="${dbList}" varStatus="status">
								<option value="<c:out value="${result.db_id}"/>" <c:if test="${result.db_id eq workInfo[0].db_id}"> selected</c:if>><c:out value="${result.db_nm}"/></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th scope="row" class="ico_t1">Work<br/>설명</th>
						<td colspan="3">
							<div class="textarea_grp">
								<textarea name="wrk_exp" id="wrk_exp" maxlength=200><c:out value="${workInfo[0].wrk_exp}"/></textarea>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="pop_cmm mt25">
			<table class="write">
				<caption>백업 등록하기</caption>
				<colgroup>
					<col style="width:80px;" />
					<col style="width:178px;" />
					<col style="width:95px;" />
					<col style="width:178px;" />
					<col style="width:95px;" />
					<col style="width:150px;" />
				</colgroup>
				<tbody>
					<tr>
						<th scope="row" class="ico_t2">저장경로</th>
						<td colspan="5"><input type="text" class="txt t4" name="save_pth" id="save_pth" value="<c:out value="${workInfo[0].save_pth}"/>"/></td>
					</tr>
					<tr>
						<th scope="row" class="ico_t2">파일포맷</th>
						<td>
							<select name="file_fmt_cd" id="file_fmt_cd" onChange="changeFileFmtCd();" class="select t5">
								<option value="">선택</option>
								<option value="TC000401"<c:if test="${workInfo[0].file_fmt_cd eq 'TC000401' }"> selected</c:if>>tar</option>
								<option value="TC000402"<c:if test="${workInfo[0].file_fmt_cd eq 'TC000402' }"> selected</c:if>>plain</option>
								<option value="TC000403"<c:if test="${workInfo[0].file_fmt_cd eq 'TC000403' }"> selected</c:if>>directory</option>
							</select>
						</td>
						<th scope="row" class="ico_t2">인코딩방식</th>
						<td>
							<select name="encd_mth_nm" id="encd_mth_nm" class="select t5">
								<option value="">선택</option>
								<c:forEach var="result" items="${incodeList}" varStatus="status">
									<option value="<c:out value="${result.sys_cd}"/>"<c:if test="${workInfo[0].encd_mth_nm eq result.sys_cd }"> selected</c:if>><c:out value="${result.sys_cd_nm}"/></option>
								</c:forEach>
							</select>
						</td>
						<th scope="row" class="ico_t2">Rolename</th>
						<td>
							<select name="usr_role_nm" id="usr_role_nm" class="select t4">
								<option value="">선택</option>
								<c:forEach var="result" items="${roleList.data}" varStatus="status">
								<option value="<c:out value="${result.rolname}"/>"<c:if test="${workInfo[0].usr_role_nm eq result.rolname }"> selected</c:if>><c:out value="${result.rolname}"/></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th scope="row" class="ico_t2">압축률</th>
						<td>
							<select name="cprt" id="cprt" class="select t4" style="width:80px;">
								<option value="0">미압축</option>
								<option value="1">1Level</option>
								<option value="2">2Level</option>
								<option value="3">3Level</option>
								<option value="4">4Level</option>
								<option value="5">5Level</option>
								<option value="6">6Level</option>
								<option value="7">7Level</option>
								<option value="8">8Level</option>
								<option value="9">9Level</option>
							</select> %</td>
						<th scope="row" class="ico_t2">파일보관일수</th>
						<td><input type="text" class="txt t6" name="file_stg_dcnt" id="file_stg_dcnt" maxlength=3 value="<c:out value="${workInfo[0].file_stg_dcnt}"/>"/> 일</td>
						<th scope="row" class="ico_t2">백업유지갯수</th>
						<td><input type="text" class="txt t6" name="bck_mtn_ecnt" id="bck_mtn_ecnt" maxlength=3 value="<c:out value="${workInfo[0].bck_mtn_ecnt}"/>"/></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="pop_cmm c2 mt25">
			<div class="addOption_grp">
				<ul class="tab">
					<li class="on"><a href="#n">부가옵션 #1</a></li>
					<li><a href="#n">부가옵션 #2</a></li>
					<li><a href="#n">object 선택</a></li>
				</ul>
				<div class="tab_view">
					<div class="view on addOption_inr">
						<ul>
							<li>
								<p class="op_tit">Sections</p>
								<div class="inp_chk">
									<span>
										<input type="checkbox" id="option_1_1" name="opt" value="Y" grp_cd="TC0006" opt_cd="TC000601" onClick="checkSection();"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0006' && optVal.opt_cd eq 'TC000601'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_1_1">Pre-data</label>
									</span>
									<span>
										<input type="checkbox" id="option_1_2" name="opt" value="Y" grp_cd="TC0006" opt_cd="TC000602" onClick="checkSection();"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0006' && optVal.opt_cd eq 'TC000602'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_1_2">data</label>
									</span>
									<span>
										<input type="checkbox" id="option_1_3" name="opt" value="Y" grp_cd="TC0006" opt_cd="TC000603" onClick="checkSection();"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0006' && optVal.opt_cd eq 'TC000603'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_1_3">Post-data</label>
									</span>
								</div>
							</li>
							<li>
								<p class="op_tit">Object 형태</p>
								<div class="inp_chk">
									<span>
										<input type="checkbox" id="option_2_1" name="opt" value="Y" grp_cd="TC0007" opt_cd="TC000701" onClick="checkObject('TC000701');"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0007' && optVal.opt_cd eq 'TC000701'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_2_1">Only data</label>
									</span>
									<span>
										<input type="checkbox" id="option_2_2" name="opt" value="Y" grp_cd="TC0007" opt_cd="TC000702" onClick="checkObject('TC000702');"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0007' && optVal.opt_cd eq 'TC000702'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_2_2">Only Schema</label>
									</span>
									<span>
										<input type="checkbox" id="option_2_3" name="opt" value="Y" grp_cd="TC0007" opt_cd="TC000703"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0007' && optVal.opt_cd eq 'TC000703'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_2_3">Blobs</label>
									</span>
								</div>
							</li>
							<li>
								<p class="op_tit">저장여부선택</p>
								<div class="inp_chk">
									<span>
										<input type="checkbox" id="option_3_1" name="opt" value="Y" grp_cd="TC0008" opt_cd="TC000801"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0008' && optVal.opt_cd eq 'TC000801'}">checked</c:if>
										</c:forEach>
										 disabled/>
										<label for="option_3_1">Owner</label>
									</span>
									<span>
										<input type="checkbox" id="option_3_2" name="opt" value="Y" grp_cd="TC0008" opt_cd="TC000802"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0008' && optVal.opt_cd eq 'TC000802'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_3_2">Privilege</label>
									</span>
									<span>
										<input type="checkbox" id="option_3_3" name="opt" value="Y" grp_cd="TC0008" opt_cd="TC000803"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0008' && optVal.opt_cd eq 'TC000803'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_3_3">Tablespace</label>
									</span>
									<span>
										<input type="checkbox" id="option_3_4" name="opt" value="Y" grp_cd="TC0008" opt_cd="TC000804"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0008' && optVal.opt_cd eq 'TC000804'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_3_4">Unlogged Table data</label>
									</span>
								</div>
							</li>
						</ul>
					</div>
					<div class="view addOption_inr">
						<ul>
							<li>
								<p class="op_tit">쿼리</p>
								<div class="inp_chk double">
									<span>
										<input type="checkbox" id="option_4_1" name="opt" value="Y" grp_cd="TC0009" opt_cd="TC000901" onClick="checkOid();"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0009' && optVal.opt_cd eq 'TC000901'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_4_1">Use Column Inserts</label>
									</span>
									<span>
										<input type="checkbox" id="option_4_2" name="opt" value="Y" grp_cd="TC0009" opt_cd="TC000902" onClick="checkOid();"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0009' && optVal.opt_cd eq 'TC000902'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_4_2">Use Insert Commands</label>
									</span>
									<span>
										<input type="checkbox" id="option_4_3" name="opt" value="Y" grp_cd="TC0009" opt_cd="TC000903" disabled
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0009' && optVal.opt_cd eq 'TC000903'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_4_3">CREATE DATABASE포함</label>
									</span>
									<span>
										<input type="checkbox" id="option_4_4" name="opt" value="Y" grp_cd="TC0009" opt_cd="TC000904" disabled
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0009' && optVal.opt_cd eq 'TC000904'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_4_4">DROP DATABASE포함</label>
									</span>
								</div>
							</li>
							<li>
								<p class="op_tit">기타</p>
								<div class="inp_chk third">
									<span>
										<input type="checkbox" id="option_5_1" name="opt" value="Y" grp_cd="TC0010" opt_cd="TC001001"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0010' && optVal.opt_cd eq 'TC001001'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_5_1">OIDS포함</label>
									</span>
									<span>
										<input type="checkbox" id="option_5_2" name="opt" value="Y" grp_cd="TC0010" opt_cd="TC001002"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0010' && optVal.opt_cd eq 'TC001002'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_5_2">인용문포함</label>
									</span>
									<span>
										<input type="checkbox" id="option_5_3" name="opt" value="Y" grp_cd="TC0010" opt_cd="TC001003"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0010' && optVal.opt_cd eq 'TC001003'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_5_3">식별자에 ""적용</label>
									</span>
									<span>
										<input type="checkbox" id="option_5_4" name="opt" value="Y" grp_cd="TC0010" opt_cd="TC001004"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0010' && optVal.opt_cd eq 'TC001004'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_5_4">Set Session authorization사용</label>
									</span>
									<span>
										<input type="checkbox" id="option_5_5" name="opt" value="Y" grp_cd="TC0010" opt_cd="TC001005"
										<c:forEach var="optVal" items="${workOptInfo}" varStatus="status">
											<c:if test="${optVal.grp_cd eq 'TC0010' && optVal.opt_cd eq 'TC001005'}">checked</c:if>
										</c:forEach>
										/>
										<label for="option_5_5">자세한 메시지 포함</label>
									</span>
								</div>
							</li>
						</ul>
					</div>
					
					<div class="view">
						<div class="tNav" >
							
						</div>
					</div>
				</div>
			</div>
			</form>
		</div>
		<div class="btn_type_02">
			<span class="btn btnC_01" onClick="fn_update_work();return false;"><button>수정등록</button></span>
			<a href="#n" class="btn" onclick="self.close();return false;"><span>취소</span></a>
		</div>
	</div>
</div>
</body>
</html>
