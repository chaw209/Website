<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    //세션연결 상태 바로 종료=>메모리상에 저장된 계정정보 삭제
    session.invalidate();
%>
<script>
     alert("정상적으로 로그아웃되었습니다.");
     //location.href="경로포함해서 이동할 페이지명"
     location.href="Login.jsp";
</script>
