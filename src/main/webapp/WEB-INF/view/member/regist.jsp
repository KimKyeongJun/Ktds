<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="/HelloSpring/js/jquery-3.3.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
	$().ready(function() {
						/* $("#email").keyup(function() {
							//Ajax 요청
							$.post("/HelloSpring/member/check/duplicate"	//URL
									, {"email" : $(this).val()}					// Request Parameter
									, function(response) {					// Response
										if( response.duplicated ){
											$("#email-error").slideDown(100);
										}
										else{
											$("#email-error").slideUp(100);
										}
										console.log(response)
									})
						}) */

		$("#registButton").click(function() {
			if ($("#email").val() == ""){
				alert("email을 입력하세요!")
				$("#email").focus()
				return;
			}
			
			if($("#name").val() == ""){
				alert("이름을 입력하세요!")
				$("#name").focus()
				return;
			}
			
			if($("#password").val() == ""){
				alert("비밀번호를 입력하세요")
				$("#password").focus()
				return;
			}
				
			$.post("/HelloSpring/member/check/validate",
					$("#registForm").serialize(),
					function(response) {
						if(response.status){
							alert("가입 완료!")
							location.href="/HelloSpring/member/login"
						}
						else{
							console.log(response.message)
							alert(response.message)
							location.href="/HelloSpring/member/regist"
						}
			})
		})
})
</script>
</head>
<body>

	<h1>회원 등록하기</h1>

	<form:form modelAttribute="memberVO" id="registForm" name ="registForm" autocomplete="off">
		<div>
			<input type="email" id="email" name="email" placeholder="이메일을 입력하세요"
				value="${sessionScope._VAILDCHECK_.email}" />
			<div id="email-error" style="display: none;">이 이메일은 사용할 수 없습니다.
			</div>
		</div>
		<div>
			<input type="text" id="name" name="name" placeholder="이름을 입력하세요"
				value="${sessionScope._VAILDCHECK_.name}" />
		</div>
		<div>
			<input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요"
				value="${sessionScope._VAILDCHECK_.password}" />
		</div>

		<div>
			<button id="registButton" name="registButton">등록하기</button>
		</div>
	</form:form>

</body>
</html>