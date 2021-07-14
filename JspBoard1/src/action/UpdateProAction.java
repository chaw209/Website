package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 // 글수정-> /updatePro.do요청
import ch.board.*;//BoardDTO,BoardDAO 때문에 필요
import java.sql.Timestamp;//추가할 부분(시간)

public class UpdateProAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		//액션클래스=>jsp에서 호출하는 메서드(자바코드)
		//한글처리
	     request.setCharacterEncoding("utf-8");
	     //추가
	     String pageNum=request.getParameter("pageNum");
	     
	     BoardDTO article=new BoardDTO();
	     
	     article.setNum(Integer.parseInt(request.getParameter("num")));
	     article.setWriter(request.getParameter("writer"));
	     article.setEmail(request.getParameter("email"));
	     article.setSubject(request.getParameter("subject"));
	     article.setContent(request.getParameter("content"));
	     article.setPasswd(request.getParameter("passwd"));
	    
	     BoardDAO dbPro=new BoardDAO();
	     int check=dbPro.updateArticle(article);//암호
	     
	     //2개의 공유값이 필요
	     request.setAttribute("pageNum", pageNum);
	     request.setAttribute("check", check);//데이터 수정성공유무
	     
	     return "/updatePro.jsp";
	}
}
