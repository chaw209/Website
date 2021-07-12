<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.sql.Timestamp,ch.board.*" %>
   
<%
    //한글처리
    request.setCharacterEncoding("utf-8");
    //BoardDTO->Setter Method(5)+hidden 객체(4)
    //BoardDTO article=new BoardDTO();
    //BoardDAO 객체필요=>insertArticle 호출하기위해서 
%>
<jsp:useBean id="article"  class="ch.board.BoardDTO" />
<jsp:setProperty name="article"  property="*" />
<%
	 //insertArticle(article)=>9개->10개(ip,작성날짜)12개(readcount,num)
	 Timestamp temp=new Timestamp(System.currentTimeMillis());//컴퓨터의 날짜,시간
	 article.setReg_date(temp);//오늘 날짜를 계산->대신에 주석달고 now()
	 article.setIp(request.getRemoteAddr());//원격 ip주소 저장
	 
	 BoardDAO dbPro=new BoardDAO();
	 dbPro.insertArticle(article);
	 //list.jsp로 페이지 이동->DB연결->웹에 출력
	 response.sendRedirect("list.jsp");
%>






