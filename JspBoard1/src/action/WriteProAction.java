package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 // �۾���-> /writePro.do��û
import ch.board.*;//BoardDTO,BoardDAO ������ �ʿ�
import java.sql.Timestamp;//�߰��� �κ�(�ð�)

public class WriteProAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		//�׼�Ŭ����=>jsp���� ȣ���ϴ� �޼���(�ڹ��ڵ�)
		//�ѱ�ó��
	     request.setCharacterEncoding("utf-8");
	  
	     BoardDTO article=new BoardDTO();
	     
	     article.setNum(Integer.parseInt(request.getParameter("num")));
	     article.setWriter(request.getParameter("writer"));
	     article.setEmail(request.getParameter("email"));
	     article.setSubject(request.getParameter("subject"));
	     article.setPasswd(request.getParameter("passwd"));
	    
	      //Timestamp temp=new Timestamp(System.currentTimeMillis());//��ǻ���� ��¥,�ð�
	      article.setReg_date(new Timestamp(System.currentTimeMillis()));//���� ��¥ ���
	      article.setRef(Integer.parseInt(request.getParameter("ref")));
	      article.setRe_step(Integer.parseInt(request.getParameter("re_step")));
	      article.setRe_level(Integer.parseInt(request.getParameter("re_level")));
	      article.setContent(request.getParameter("content"));
	      article.setIp(request.getRemoteAddr());//����ip�ּ� ����
	      
	      BoardDAO dbPro=new BoardDAO();
	      dbPro.insertArticle(article);
	      //response.sendRedirect("http://localhost:8090/JspBoard1/list.do");
	      
		return "/writePro.jsp";
	}

}
