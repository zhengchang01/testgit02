<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-cn">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/admin/css/Style.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="${pageContext.request.contextPath}/admin/js/public.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/client/js/jquery-1.8.3.min.js"></script>
<style type="text/css">
.tdstyle{
    width:100px;
    height:40px;
    border :2px solid black;
    font-size:20px;
    text-align :center;
}
</style>
<script type="text/javascript">
var year;
var yearMsg;
var month;
var download = true;
window.onload = function() {
	year = document.getElementById("year");
	yearMsg = document.getElementById("yearMsg");
	month = document.getElementById("month");
}

function checkForm(){
	var byear = checkMsg();
	if(!download){
		var str = confirm("你想要下载的数据为空，是否下载");
		if(str == true){
			return true;
		}else{
			return false;
		}
	}else{
		return byear;
	}
}

function checkMonth(){
	var yearValue = year.value;
	if(yearValue != "0"){
		checkYear();
	}
	
}

function checkMsg(){
    var regex = /^\d{4}$/;
    var value = year.value;
    var msg = "";
    if (!value)
		msg = "年份必须填写：";
	else if (!regex.test(value))
		msg = "年份格式不合法：";
    yearMsg.style.color = msg == "" ? "black" : "red";
	return msg == "";
}

function checkYear(){
	
	var byear = checkMsg();
	if(byear){
		var yearObj = year.value;
		var monthObj = month.value;
		$.ajax({
	    	type:"POST",
			url:"${pageContext.request.contextPath }/admin/products/findProductByTime.do",
			dataType : "json",
			async : false,
			data:{
				year:yearObj,
				month:monthObj
			},
	    	success:function(data){
	    		if(data.length == 0){
	    		download=false;
	    		}
	    		var insertText;
	    		
	    		if(monthObj != "0"){
	    			insertText = "<tr><td class='tdstyle' colspan='2' style='color:blue; font-size:22px;'>"+yearObj+"年"+monthObj+"月销售榜单</td></tr><tr"+"><td class='tdstyle'"+">名称</td><td class='tdstyle'"+">销售数量</td></tr>";
	    		}else{
	    			insertText = "<tr><td class='tdstyle' colspan='2' style='color:blue; font-size:22px;'>"+yearObj+"年销售榜单</td></tr><tr"+"><td class='tdstyle'"+">名称</td><td class='tdstyle'"+">销售数量</td></tr>";
	    		}
	    		
	    		for(var i=0;i<data.length;i++){
	    			var ls = data[i];
	    			insertText +="<tr"+"><td class='tdstyle'"+">"+ls.name+"</td><td class='tdstyle'"+">"+ls.salnum+"</td></tr>";
	    			}
	    		document.getElementById("ulul").innerHTML=insertText;   //在html页面id=ulul的标签里显示html内容
	    	}
	    });
	}
}



</script>
</head>
<body>
	<br>
	<form id="Form1" name="Form1" action="${pageContext.request.contextPath}/admin/products/download.do" method="post" onsubmit="return checkForm();">
		<table cellSpacing="1" cellPadding="0" width="100%" align="center" 
		bgColor="#f5fafe" border="0">
			<tbody>
				<tr>
					<td class="ta_01" align="center" bgColor="#afd1f3">
						<strong>查 询 条 件</strong>
					</td>
				</tr>
				<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td id="yearMsg" height="22" align="center" bgColor="#f5fafe" class="ta_01">
									请输入年份
								</td>
								<td class="ta_01" bgColor="#ffffff">
									<input type="text" name="year" size="15" value="" onkeyup="checkYear();"
									id="year" class="bg" />
								</td>
								<td height="22" align="center" bgColor="#f5fafe" class="ta_01">
									请选择月份
								</td>
								<td class="ta_01" bgColor="#ffffff">
									<select name="month" id="month" onchange="checkMonth();">
										<option value="0">--选择月份--</option>
										<option value="1">一月</option>
										<option value="2">二月</option>
										<option value="3">三月</option>
										<option value="4">四月</option>
										<option value="5">五月</option>
										<option value="6">六月</option>
										<option value="7">七月</option>
										<option value="8">八月</option>
										<option value="9">九月</option>
										<option value="10">十月</option>
										<option value="11">十一月</option>
										<option value="12">十二月</option>
									</select>
								</td>
							</tr>
							<tr>
								<td width="100" height="22" align="center" 
								bgColor="#f5fafe" class="ta_01">
								</td>
								<td class="ta_01" bgColor="#ffffff">
									<font face="宋体" color="red"> &nbsp;</font>
								</td>
								<td align="right" bgColor="#ffffff" class="ta_01">
									<br><br>
								</td>
								<td align="center" bgColor="#ffffff" class="ta_01">
									<input type="submit" id="search" name="search" value="下载"
									class="button_view"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
									<input type="reset" name="reset" value="重置" class="button_view" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
	<table id="ulul" cellSpacing="1" cellPadding="0" width="70%" align="center" 
		bgColor="#f5fafe" border="0" style="border-collapse: collapse;">
			
	</table>
</body>
</html>

