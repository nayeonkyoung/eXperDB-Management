<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ include file="../../cmmn/commonLocale.jsp"%>
<%
	/**
	* @Class Name : dataRegForm.jsp
	* @Description : 데이터 이행 등록 화면
	* @Modification Information
	*
	*   수정일         수정자                   수정내용
	*  ------------    -----------    ---------------------------
	*  2019.09.18     최초 생성
	*
	* author kimjy
	* since 2019.09.18
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
$(window.document).ready(function() {
	 if("${exrt_trg_tb_cnt}">0){
		 $("#src_tables option:eq(0)").attr("selected", "selected");
		 $("#src_include_tables").val("${exrt_trg_tb_cnt}개");
		 $("#include").show();
		 $("#exclude").hide();
	 }else if("${exrt_exct_tb_cnt}">0){
		 $("#src_tables option:eq(1)").attr("selected", "selected");
		 $("#src_exclude_tables").val("${exrt_exct_tb_cnt}개");
		 $("#exclude").show();
		 $("#include").hide(); 
	 }	
});

/* ********************************************************
 * Validation Check
 ******************************************************** */
function valCheck(){
	if($("#db2pg_trsf_wrk_exp").val() == ""){
		alert('<spring:message code="message.msg108" />');
		$("#db2pg_trsf_wrk_exp").focus();
		return false;
	}else if($("#db2pg_source_system_id").val() == ""){
		alert("소스 시스템정보를 등록해주세요.");
		$("#db2pg_source_system_id").focus();
		return false;
	}else if($("#db2pg_trg_sys_id").val() == ""){
		alert("타겟 시스템정보를 등록해주세요.");
		$("#db2pg_trg_sys_id").focus();
		return false;
	}else{
		return true;
	}
}

/* ********************************************************
 * 수정 버튼 클릭시
 ******************************************************** */
function fn_update_work(){
	if(valCheck()){
		$.ajax({
			url : "/db2pg/updateDataWork.do",
		  	data : {
		  		db2pg_trsf_wrk_id : "${db2pg_trsf_wrk_id}",
		  		db2pg_trsf_wrk_nm : $("#db2pg_trsf_wrk_nm").val().trim(),
		  		db2pg_trsf_wrk_exp : $("#db2pg_trsf_wrk_exp").val(),
		  		db2pg_src_sys_id : $("#db2pg_sys_id").val(),
		  		db2pg_trg_sys_id : $("#db2pg_trg_sys_id").val(),
		  		exrt_dat_cnt : $("#exrt_dat_cnt").val(),
		  		src_include_tables : $("#src_include_table_nm").val(),
		  		src_exclude_tables : $("#src_exclude_table_nm").val(),
		  		exrt_dat_ftch_sz : $("#exrt_dat_ftch_sz").val(),
		  		dat_ftch_bff_sz : $("#dat_ftch_bff_sz").val(),
		  		exrt_prl_prcs_ecnt : $("#exrt_prl_prcs_ecnt").val(),
		  		lob_dat_bff_sz : $("#lob_dat_bff_sz").val(),
		  		tb_rbl_tf : $("#tb_rbl_tf").val(),
		  		ins_opt_cd : $("#ins_opt_cd").val(),
		  		cnst_cnd_exrt_tf : $("#cnst_cnd_exrt_tf").val(),
		  		src_cnd_qry : $("#src_cnd_qry").val(),
		  		usr_qry_use_tf : $('input[name="usr_qry_use_tf"]:checked').val(),
		  		db2pg_usr_qry : $("#db2pg_usr_qry").val(),
		  		wrk_id : $("#wrk_id").val()
		  	},
			type : "post",
			beforeSend: function(xhr) {
		        xhr.setRequestHeader("AJAX", true);
		     },
			error : function(xhr, status, error) {
				if(xhr.status == 401) {
					alert('<spring:message code="message.msg02" />');
					top.location.href = "/";
				} else if(xhr.status == 403) {
					alert('<spring:message code="message.msg03" />');
					top.location.href = "/";
				} else {
					alert("ERROR CODE : "+ xhr.status+ "\n\n"+ "ERROR Message : "+ error+ "\n\n"+ "Error Detail : "+ xhr.responseText.replace(/(<([^>]+)>)/gi, ""));
				}
			},
			success : function(result) {
				if(result.resultCode == "0000000000"){
					alert('<spring:message code="message.msg07" /> ');
					opener.getdataDataList();
					self.close();
				}else{
					alert('등록에 실패했습니다.');
				}		
			}
		});
	}
}

/* ********************************************************
 * 사용자쿼리 체크박스 제어
 ******************************************************** */
function fn_checkBox(result){
	if(result == 'true'){
		$("#db2pg_usr_qry").removeAttr("readonly");
	}else{
		$('#db2pg_usr_qry').val('');
		$('#db2pg_usr_qry').attr('readonly', true);
	}
}

/* ********************************************************
 * DBMS 서버 호출하여 입력
 ******************************************************** */
 function fn_dbmsAddCallback(db2pg_sys_id,db2pg_sys_nm){
	 $('#db2pg_sys_id').val(db2pg_sys_id);
	 $('#db2pg_source_system_nm').val(db2pg_sys_nm);
}

/* ********************************************************
  * DBMS 서버(PG) 호출하여 입력
  ******************************************************** */
  function fn_dbmsPgAddCallback(db2pg_sys_id,db2pg_sys_nm){
 	 $('#db2pg_trg_sys_id').val(db2pg_sys_id);
 	 $('#db2pg_trg_sys_nm').val(db2pg_sys_nm);
 }
 
/* ********************************************************
 * 소스시스템 등록 버튼 클릭시
 ******************************************************** */
function fn_dbmsInfo(){
	var popUrl = "/db2pg/popup/dbmsInfo.do";
	var width = 965;
	var height = 680;
	var left = (window.screen.width / 2) - (width / 2);
	var top = (window.screen.height /2) - (height / 2);
	var popOption = "width="+width+", height="+height+", top="+top+", left="+left+", resizable=no, scrollbars=yes, status=no, toolbar=no, titlebar=yes, location=no,";
	
	var winPop = window.open(popUrl,"dbmsInfoPop",popOption);
}

/* ********************************************************
 * 타겟시스템(PG) 등록 버튼 클릭시
 ******************************************************** */
function fn_dbmsPgInfo(){
	var popUrl = "/db2pg/popup/dbmsPgInfo.do";
	var width = 965;
	var height = 680;
	var left = (window.screen.width / 2) - (width / 2);
	var top = (window.screen.height /2) - (height / 2);
	var popOption = "width="+width+", height="+height+", top="+top+", left="+left+", resizable=no, scrollbars=yes, status=no, toolbar=no, titlebar=yes, location=no,";
	
	var winPop = window.open(popUrl,"dbmsPgInfo",popOption);
}

/* ********************************************************
 * 추출 대상 테이블, 추출 제외 테이블 등록 버튼 클릭시
 ******************************************************** */
function fn_tableList(gbn){
	if($('#db2pg_source_system_nm').val() == ""){
		alert("소스시스템을 선택해주세요.");
		return false;
	}
	
	var frmPop= document.frmPopup;
	var url = '/db2pg/popup/tableInfo.do';
	window.open('','popupView','width=930, height=850');
	     
	frmPop.action = url;
	frmPop.target = 'popupView';
	frmPop.db2pg_sys_id.value = $('#db2pg_sys_id').val();
	frmPop.tableGbn.value = gbn;
	if(gbn == 'include'){
		frmPop.src_include_table_nm.value = $('#src_include_table_nm').val();  
	}else{
		frmPop.src_exclude_table_nm.value = $('#src_exclude_table_nm').val();  
	}
	frmPop.submit();   
}

/* ********************************************************
 * select box control
 ******************************************************** */
 $(function() {
	 $("#src_tables").change(function(){
		 $("#src_include_tables").val("");
		 $("#src_exclude_tables").val("");
		 $("#src_include_table_nm").val("");
		 $("#src_exclude_table_nm").val("");
		    if(this.value=="include"){
		        $("#include").show();
			    $("#exclude").hide(); 
		    }else{
		        $("#exclude").show();
			    $("#include").hide(); 
		    }
		});
 });


function fn_tableAddCallback(rowList, tableGbn){
	if(tableGbn == 'include'){
		$('#src_include_tables').val(rowList.length+"개");
		$('#src_include_table_nm').val(rowList);
	}else{
		$('#src_exclude_tables').val(rowList.length+"개");
		$('#src_exclude_table_nm').val(rowList);
	}
}
</script>
</head>
<body>
<form name="frmPopup">
	<input type="hidden" name="db2pg_sys_id"  id="db2pg_sys_id" value="${db2pg_sys_id}">
	<input type="hidden" name="db2pg_trg_sys_id" id="db2pg_trg_sys_id" value="${db2pg_trg_sys_id}"/>
	<input type="hidden" name="src_include_table_nm"  id="src_include_table_nm" value="${exrt_trg_tb_nm}">
	<input type="hidden" name="src_exclude_table_nm"  id="src_exclude_table_nm" value="${exrt_exct_tb_nm}">
	<input type="hidden" name="tableGbn"  id="tableGbn" >
</form>
<div class="pop_container">
	<div class="pop_cts">
		<p class="tit">데이터이행 등록</p>
		<div class="pop_cmm">
			<table class="write">
				<caption>데이터이행 등록</caption>
				<colgroup>
					<col style="width:105px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th scope="row" class="ico_t1"><spring:message code="common.work_name" /></th>
						<td><input type="text" class="txt" name="db2pg_trsf_wrk_nm" id="db2pg_trsf_wrk_nm" value="${db2pg_trsf_wrk_nm}" maxlength="20" onkeyup="fn_checkWord(this,20)" placeholder="20<spring:message code='message.msg188'/>" onblur="this.value=this.value.trim()" readonly="readonly"/>
							<input type="hidden" name="wrk_id" id="wrk_id" value="${wrk_id}">
						</td>
					</tr>
					<tr>
						<th scope="row" class="ico_t1"><spring:message code="common.work_description" /></th>
						<td>
							<div class="textarea_grp">
								<textarea name="db2pg_trsf_wrk_exp" id="db2pg_trsf_wrk_exp" maxlength="25" onkeyup="fn_checkWord(this,25)" placeholder="25<spring:message code='message.msg188'/>"><c:out value="${db2pg_trsf_wrk_exp}"/></textarea>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="pop_cmm mt25">
		<div class="sub_tit"><p>시스템정보</p></div>
			<table class="write">
				<colgroup>
					<col style="width:105px;" />
					<col />
					<col style="width:105px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th scope="row" class="ico_t1">소스시스템</th>
						<td><input type="text" class="txt" name="db2pg_source_system_nm" id="db2pg_source_system_nm" value="${db2pg_source_system_nm}" placeholder="등록 버튼을 눌러주세요" readonly="readonly"/>
							<span class="btn btnC_01"><button type="button" class= "btn_type_02" onclick="fn_dbmsInfo()" style="width: 60px; margin-right: -60px; margin-top: 0;">등록</button></span>							
						</td>
					</tr>
					<tr>
					<th scope="row" class="ico_t1">타겟시스템</th>
						<td><input type="text" class="txt" name="db2pg_trg_sys_nm" id="db2pg_trg_sys_nm" value="${db2pg_trg_sys_nm}" placeholder="등록 버튼을 눌러주세요" readonly="readonly"/>
							<span class="btn btnC_01"><button type="button" class= "btn_type_02" onclick="fn_dbmsPgInfo()" style="width: 60px; margin-right: -60px; margin-top: 0;">등록</button></span>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="pop_cmm c2 mt25">
		<div class="sub_tit"><p>소스옵션</p></div>
			<div class="addOption_grp">
				<ul class="tab">
					<li class="on"><a href="#n">옵션 #1</a></li>
					<li><a href="#n">옵션 #2</a></li>
					<li><a href="#n">옵션 #3</a></li>
				</ul>
				<div class="tab_view">
					<div class="view on addOption_inr">	
						<table class="write">
							<caption>옵션정보</caption>
							<colgroup>
								<col style="width:28%" />
								<col style="width:30%" />
								<col style="width:32%" />
								</col>
							</colgroup>
							<tbody>
								<tr>
									<th scope="row" class="ico_t2">
										<select name="src_tables" id="src_tables" class="select t5" style="width: 176px;" >
											<option value="include">추출 대상 테이블</option>
											<option value="exclude">추출 제외 테이블</option>
										</select>
									</th>
									<td colspan="2">
										<div id="include">
											<input type="text" class="txt" name="src_include_tables" id="src_include_tables" readonly="readonly" />
											<span class="btn btnC_01"><button type="button" class= "btn_type_02" onclick="fn_tableList('include')" style="width: 60px; margin-right: -60px; margin-top: 0;">등록</button></span>		
										</div>
										<div id="exclude" style="display: none;">
											<input type="text" class="txt" name="src_exclude_tables" id="src_exclude_tables" readonly="readonly" />
											<span class="btn btnC_01"><button type="button" class= "btn_type_02" onclick="fn_tableList('exclude')" style="width: 60px; margin-right: -60px; margin-top: 0;">등록</button></span>												
										</div>
									</td>
								</tr>
								<tr>
									<th scope="row" class="ico_t2">추출 데이터 Fetch 사이즈</th>
									<td><input type="number" class="txt t8" name="exrt_dat_ftch_sz" id="exrt_dat_ftch_sz" value="${exrt_dat_ftch_sz}" min="3000"/></td>
									<th scope="row" class="ico_t2">데이터 Fetch 버퍼 사이즈(단위 MIB)</th>
									<td><input type="number" class="txt t8" name="dat_ftch_bff_sz" id="dat_ftch_bff_sz" value="${dat_ftch_bff_sz}" min="10"/></td>
								</tr>
								<tr>
									<th scope="row" class="ico_t2">추출 병렬처리 개수</th>
									<td><input type="number" class="txt t8" name="exrt_prl_prcs_ecnt" id="exrt_prl_prcs_ecnt" value="${exrt_prl_prcs_ecnt}" min="1"/></td>
									<th scope="row" class="ico_t2">LOB 데이터 LOB 버퍼 사이즈(단위 MIB)</th>
									<td><input type="number" class="txt t8" name="lob_dat_bff_sz" id="lob_dat_bff_sz" value="${lob_dat_bff_sz}" min="100"/></td>
								</tr>
								<tr>
									<th scope="row" class="ico_t2">테이블에서 추출할 데이터 건수</th>
									<td><input type="number" class="txt t8" name="exrt_dat_cnt" id="exrt_dat_cnt" value="${exrt_dat_cnt}" min="-1"/></td>
								</tr>								
							</tbody>
						</table>
					</div>
					<div class="view addOption_inr">
						<ul>
							<li style="border-bottom: none;">
								<p class="op_tit" style="width: 200PX;">추출 조건(WHERE문 제외)</p>
								<span>
									<div class="textarea_grp">
										<textarea name="src_cnd_qry" id="src_cnd_qry" style="height: 250px; width: 700px;"><c:out value="${src_cnd_qry}"/></textarea>
									</div>
								</span>
							</li>
						</ul>
					</div>
					<div class="view addOption_inr">
						<ul>
							<li style="border-bottom: none;">
								<p class="op_tit" style="width: 70px;">사용여부</p>
								<div class="inp_rdo">
									<input name="usr_qry_use_tf" id="rdo_r_1" type="radio" value="true" onchange="fn_checkBox('true')">
										<label for="rdo_r_1">사용</label> 
									<input name="usr_qry_use_tf" id="rdo_r_2" type="radio" value="false" checked="checked" onchange="fn_checkBox('false')"> 
										<label for="rdo_r_2">미사용</label>
								</div>
							</li>
							<li style="border-bottom: none;">
								<p class="op_tit">사용자 쿼리</p>
								<span>
									<div class="textarea_grp">
										<textarea name="db2pg_usr_qry" id="db2pg_usr_qry" style="height: 250px; width: 700px;" readonly="readonly"></textarea>
									</div>
								</span>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<div class="pop_cmm mt25">
		<div class="sub_tit"><p>타겟옵션</p></div>
			<table class="write">
				<caption><spring:message code="dashboard.Register_backup" /></caption>
				<colgroup>
					<col style="width:18%;" />
					<col style="width:17%;" />
					<col style="width:10%;" />
					<col style="width:15%;" />
					<col style="width:18%;" />
					<col style="width:17%;" />
					</col>
				</colgroup>
				<tbody>
					<tr>
						<th scope="row" class="ico_t2">테이블 리빌드 여부</th>
						<td>
							<select name="tb_rbl_tf" id="tb_rbl_tf" class="select t4">
								<c:forEach var="codeTF" items="${codeTF}">
									<option value="${codeTF.sys_cd_nm}" ${tb_rbl_tf eq codeTF.sys_cd_nm ? "selected='selected'" : ""}>${codeTF.sys_cd_nm}</option>
								</c:forEach>
							</select>
						</td>
						<th scope="row" class="ico_t2">입력모드</th>
						<td>
							<select name="ins_opt_cd" id="ins_opt_cd" class="select t4">
								<c:forEach var="codeInputMode" items="${codeInputMode}">
									<option value="${codeInputMode.sys_cd_nm}" ${ins_opt_cd eq codeInputMode.sys_cd_nm ? "selected='selected'" : ""}>${codeInputMode.sys_cd_nm}</option>
								</c:forEach>
							</select>
						</td>
						<th scope="row" class="ico_t2">제약조건 추출 여부</th>
						<td>
							<select name="cnst_cnd_exrt_tf" id="cnst_cnd_exrt_tf" class="select t4">
								<c:forEach var="codeTF" items="${codeTF}">
									<option value="${codeTF.sys_cd_nm}" ${cnst_cnd_exrt_tf eq codeTF.sys_cd_nm ? "selected='selected'" : ""}>${codeTF.sys_cd_nm}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="btn_type_02">
			<span class="btn btnC_01" onClick="fn_update_work();"><button type="button"><spring:message code="common.modify" /></button></span>
			<a href="#n" class="btn" onclick="self.close();"><span><spring:message code="common.cancel" /></span></a>
		</div>
	</div>
</div>
</body>
</html>
 