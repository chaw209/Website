package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 // 글수정-> /updatePro.do요청
import ch.board.*;//BoardDTO,BoardDAO 때문에 필요
import java.sql.Timestamp;//추가할 부분(시간)

public class DeleteProAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		int num=Integer.parseInt(request.getParameter("num"));//메서드의 매개변수 때문에
	    String pageNum=request.getParameter("pageNum");//삭제된 게시물이 있는 페이지로 이동
	     //추가
	    String passwd=request.getParameter("passwd");
	    System.out.println("deletePro.do의 num="+num+", pageNum="+pageNum);
	    
	    BoardDAO dbPro=new BoardDAO();
	    int check=dbPro.deleteArticle(num, passwd);
	     
	    //2개의 공유값이 필요
	    request.setAttribute("check", check);
	    request.setAttribute("pageNum", pageNum);
	     
	 return "/deletePro.jsp";//deletePro.jsp의 내용 updatePro.jsp의 내용이 똑같다.
	}
}
