<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="hewon.MemberDAO" %>
 <%
    //jsp내용을 수정=>바로 반영이 되도록 서버의 메모리를 제거시키는 방법
    response.setHeader("Cache-Control","no-cache");//메모리 제거(키명,설정한값)
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires",0);//보관 X
 %> 
<%
    //IdCheck.jsp?mem_id="+id
    String mem_id=request.getParameter("mem_id");
    System.out.println("IdCheck.jsp의 mem_id=>"+mem_id);
    //MemberDAO 객체를 생성->checkId()호출=>반환값 받아야 된다.
    MemberDAO memMgr=new MemberDAO();	
    boolean check=memMgr.checkId(mem_id);
%>
<html>
<body bgcolor="#FFFFCC">
<br>
<center>
<b><%=mem_id %></b>
<%
   if(check){//이미 존재하는 아이디라면
	   out.println("는 이미 존재하는 아이디입니다.<p>");
   }else{//존재하는 아이디가 아니라면
	   out.println("는 사용가능한 아이디입니다.<p>");
   }
%>
<!-- 자바스크립트에서 자기 자신을 가리키는 예약어 self -->
<a href="#" onclick="self.close()">닫기</a>
</center>
</body>
</html>






