<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div>
	${sessionScope._USER_.name} (${sessionScope._USER_.email}) 님

	Point : ${sessionScope._USER_.point}
</div>

<div>
	<a href="/HelloSpring/member/logout">Logout</a>
</div>