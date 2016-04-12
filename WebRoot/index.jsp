<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
		<title></title>
	</head>
	<body>
		<button id="gettoken">点击ajax获取token</button>
		<textarea id="token" rows="5" cols="25" style="width: 300px;" placeholder="token值"></textarea>
		<br />
		<br />
		<button id="validtoken">点击验证上面的token</button>
		<textarea id="result" readonly rows="5" cols="25" style="width: 300px;" placeholder="验证结果"></textarea>
		
		
		<script src="jquery-2.1.0.js" type="text/javascript" charset="utf-8"></script>
		<script>
			$(function () {
				$("#gettoken").on("click",function () {
					$.ajax({
						type:"put",
						url:"http://localhost:8080/JWT/author/token",
						async:true,
						success:function(data){
							$("#token").val(data);
						}
					});
				})
				
				
				$("#validtoken").on('click',function (e) {
					var token=$.trim($("#token").val());
					if(!token.length){
						alert("请先获取token")
						return;
					}
					$.ajax({
						type:"get",
						url:"http://localhost:8080/JWT/author/token?r="+Math.random(),
						async:true,
						beforeSend: function(request) {
	                        request.setRequestHeader("token", token);
	                    },
						success:function (data) {
							$("#result").val(data);
						}
					});
				})
				
			})
		</script>
	</body>
</html>

