package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteFormAction implements CommandAction {

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		//conent.jsp->������ư->deleteForm.do?num=2&pageNum=1->deletePro.jsp(deleteArticle)
	     int num=Integer.parseInt(request.getParameter("num"));//�޼����� �Ű�����������
	     String pageNum=request.getParameter("pageNum");//������ �Խù��� �ִ� �������� �̵�
	     System.out.println("deleteForm.do�� num=>"+num+",pageNum=>"+pageNum);
	     
	     request.setAttribute("num", num);//������ �Խù���ȣ
	     request.setAttribute("pageNum", pageNum);
	     
		return "/deleteForm.jsp";
	}
}
