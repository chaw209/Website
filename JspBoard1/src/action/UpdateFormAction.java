package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.board.*;

// /updateForm.do=> updateForm.jsp
public class UpdateFormAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
	    //content.jsp->글수정버튼 클릭->updateForm.do?num=3&pageNum=1
	   int num=Integer.parseInt(request.getParameter("num"));
	   String pageNum=request.getParameter("pageNum");
	   BoardDAO dbPro=new BoardDAO();
	   //select * from board where num=?
	   BoardDTO article=dbPro.updateGetArticle(num);//조회수가 증가X
	   
	   //2.서버의 메모리에 저장
	   request.setAttribute("pageNum", pageNum);//${pageNum}
	   request.setAttribute("article", article);//num은 article에 포함
	   
	   return "/updateForm.jsp";
	}
}
