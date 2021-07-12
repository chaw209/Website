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
      request.setCharacterEncoding("utf-8");//한글데이터 수정
    //추가
    String mem_id=request.getParameter("mem_id");//null
    System.out.println("MemberUpdate.jsp의 mem_id=>"+mem_id);
 %>
 <jsp:useBean id="mem" class="hewon.MemberDTO" />
 <jsp:setProperty name="mem" property="*" /> 
<%
    //MemberDAO 객체를 생성->memberUpdate()호출=>반환값 받아야 된다.
    MemberDAO memMgr=new MemberDAO();	
    boolean check=memMgr.memberUpdate(mem);
%>
<html>
<body bgcolor="#FFFFCC">
<br>
<center>
<%
   if(check){//회원수정에 성공했다면
 %>
<script>
    alert("성공적으로 수정되었습니다.")
    location.href="Login.jsp" 
</script>
<%   }else{ %>
<script>
   alert("수정도중 에러가 발생되었습니다.")
   history.back();
</script>
<% }%>
</center>
</body>
</html>






