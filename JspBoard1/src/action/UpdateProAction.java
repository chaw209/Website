package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 // �ۼ���-> /updatePro.do��û
import ch.board.*;//BoardDTO,BoardDAO ������ �ʿ�
import java.sql.Timestamp;//�߰��� �κ�(�ð�)

public class UpdateProAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		//�׼�Ŭ����=>jsp���� ȣ���ϴ� �޼���(�ڹ��ڵ�)
		//�ѱ�ó��
	     request.setCharacterEncoding("utf-8");
	     //�߰�
	     String pageNum=request.getParameter("pageNum");
	     
	     BoardDTO article=new BoardDTO();
	     
	     article.setNum(Integer.parseInt(request.getParameter("num")));
	     article.setWriter(request.getParameter("writer"));
	     article.setEmail(request.getParameter("email"));
	     article.setSubject(request.getParameter("subject"));
	     article.setContent(request.getParameter("content"));
	     article.setPasswd(request.getParameter("passwd"));
	    
	     BoardDAO dbPro=new BoardDAO();
	     int check=dbPro.updateArticle(article);//��ȣ
	     
	     //2���� �������� �ʿ�
	     request.setAttribute("pageNum", pageNum);
	     request.setAttribute("check", check);//������ ������������
	     
	     return "/updatePro.jsp";
	}
}
