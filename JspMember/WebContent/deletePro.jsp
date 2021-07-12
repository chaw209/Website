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
    //추가
    String mem_id=request.getParameter("mem_id");//입력x->hidden객체
    String passwd=request.getParameter("passwd");//입력 0
    System.out.println("deletePro.jsp의 mem_id="+mem_id+", passwd="+passwd);
 %>
<%
    //MemberDAO 객체를 생성->memberDelete()호출=>반환값 받아야 된다.
    MemberDAO memMgr=new MemberDAO();	
    int check=memMgr.memberDelete(mem_id,passwd);
    System.out.println("deletePro.jsp의 회원삭제유무(check)="+check);
%>
<%
        if(check==1){//회원탈퇴에 성공했다면
        	session.invalidate();//세션연결해제
    %>
      <script>
           alert('<%=mem_id%>님 성공적으로 탈퇴처리 되었습니다.');
           location.href="Login.jsp";//mem_id=null->id,passwd인증창
      </script>  	
   <% }else {%>
        <script>
          alert("비밀번호가 틀립니다.\n 다시 한번 확인하시기 바랍니다.");
          history.back();//history.go(-1)
       </script>
   <% } %>






