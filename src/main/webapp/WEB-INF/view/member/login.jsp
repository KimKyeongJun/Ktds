<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<h1>회원 로그인</h1>

	<form:form modelAttribute="memberVO" method="POST" action="/HelloSpring/member/login">
		<div>
			<input type="email" name="email" placeholder="Email" value="${memberVO.email}"/>
		</div>
		<div>
			<form:errors path="email"/>
		</div>
		<div>
			<input type="password" name="password" placeholder="Password" value="${memberVO.password}" />
		</div>
		<div>
			<form:errors path="password"/>
			<form:errors path="name"/>
		</div>
		<div>
			<input type="submit" value="로그인" />
		</div>

	</form:form>

</body>
</html>