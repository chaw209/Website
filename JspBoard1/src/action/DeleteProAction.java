package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 // �ۼ���-> /updatePro.do��û
import ch.board.*;//BoardDTO,BoardDAO ������ �ʿ�
import java.sql.Timestamp;//�߰��� �κ�(�ð�)

public class DeleteProAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		int num=Integer.parseInt(request.getParameter("num"));//�޼����� �Ű����� ������
	    String pageNum=request.getParameter("pageNum");//������ �Խù��� �ִ� �������� �̵�
	     //�߰�
	    String passwd=request.getParameter("passwd");
	    System.out.println("deletePro.do�� num="+num+", pageNum="+pageNum);
	    
	    BoardDAO dbPro=new BoardDAO();
	    int check=dbPro.deleteArticle(num, passwd);
	     
	    //2���� �������� �ʿ�
	    request.setAttribute("check", check);
	    request.setAttribute("pageNum", pageNum);
	     
	 return "/deletePro.jsp";//deletePro.jsp�� ���� updatePro.jsp�� ������ �Ȱ���.
	}
}
