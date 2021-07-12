<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="hewon.MemberDAO" %>

<jsp:useBean id="memMgr" class="hewon.MemberDAO" />     
<%
    String mem_id=request.getParameter("mem_id");
    String mem_passwd=request.getParameter("mem_passwd");
    System.out.println("mem_id=>"+mem_id+
    		                    ",mem_passwd=>"+mem_passwd);
    //MemberDAO 객체를 생성->loginCheck()호출=>반환값 받아야 된다.
    //MemberDAO memMgr=new MemberDAO();	
    boolean check=memMgr.loginCheck(mem_id, mem_passwd);
%>
<%
   //check->LoginSuccess.jsp(인증화면),LogError.jsp
   if(check){//if(check==true) 인증성공
	   session.setAttribute("idKey",mem_id);//키명,저장할값(id계정)
	   //response.sendRedirect("LoginSuccess.jsp");//인증화면
	   response.sendRedirect("Login.jsp");
   }else{//id가 없다면
	   response.sendRedirect("LogError.jsp");//파일명 틀리면 404 error
   }
%>







