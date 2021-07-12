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
      request.setCharacterEncoding("utf-8");
 //추가
    String mem_id=request.getParameter("mem_id");
    System.out.println("MemberInsert.jsp의 mem_id=>"+mem_id);//kkk
 %>
 <jsp:useBean id="mem" class="hewon.MemberDTO" />
 <jsp:setProperty name="mem" property="*" /> 
<%
    mem.setMem_id(request.getParameter("mem_id"));
    //MemberDAO 객체를 생성->memberInsert()호출=>반환값 받아야 된다.
    MemberDAO memMgr=new MemberDAO();	
    boolean check=memMgr.memberInsert(mem);
%>
<html>
<body bgcolor="#FFFFCC">
<br>
<center>
<%
   if(check){//회원가입에 성공했다면
	   out.println("<b>회원가입을 축하드립니다.</b><p>");
       out.println("<a href=Login.jsp>로그인</a>");
   }else{//회원가입 실패
	   out.println("<b>다시 입력하여 주십시오.</b><p>");
	   out.println("<a href=Register.jsp>다시 가입</a>");
   }
%>
</center>
</body>
</html>






