package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//�𵨿� ���� Ŭ������ �ҷ����� ���ؼ� import
import ch.board.*;//BoardDAO
import java.util.*;//List

public class ListAction implements CommandAction {

	// /list.do�� ��û�� �������� ó�����ִ� �޼���
	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		//1.jsp���� ����ߴ� �ڹ��ڵ带 ���� �׼�Ŭ������ �̵�
	     int pageSize=5;//numPerPage->�������� �����ִ� �Խù���(=���ڵ��)
	     int blockSize=3;//pagePerBlock->���� �����ִ� �������� 
	    
	    //�Խ����� �� ó�� �����Ű�� ������ 1���������� ���
	    String pageNum=request.getParameter("pageNum"); 
	    if(pageNum==null){
	    	pageNum="1";//default(������ 1�������� �������� �ʾƵ� ������� �Ǳ⶧����),���� �ֱ��� ��
	    }
	    //����������(Ŭ���ؼ� ���� �ִ� ������)=nowPage
	    int currentPage=Integer.parseInt(pageNum);//"1"->1 
	    //�޼��� ȣ��->���� ���ڵ��ȣ
	    //                   (1-1)*10+1=1, (2-1)*10+1=11, (3-1)*10+1=21
		int startRow=(currentPage-1)*pageSize+1; //���� ���ڵ� ��ȣ
		//                 1*10=10,2*10=20,3*10=30
	    int endRow=currentPage*pageSize;//������ ���ڵ� ��ȣ
	    int count=0;//�ѷ��ڵ�� 
	    int number=0;//beginPerPage->���������� �����ϴ� �� ó���� ������ �Խù���ȣ
	    List articleList=null;//ȭ�鿡 ����� ���ڵ带 ������ ����(=��ü)
	    
	    BoardDAO dbPro=new BoardDAO();
	    count=dbPro.getArticleCount();//select count(*) from board->member
	    System.out.println("���� ���ڵ��(count)=>"+count);
	    if(count > 0){
	    	//ù��° ���ڵ�, �ҷ��� ����
	    	articleList=dbPro.getArticles(startRow, pageSize);//endRow(x)
	    }else {//count=0
	    	articleList=Collections.EMPTY_LIST;//�ƹ��͵� ���� �� list��ü�� ��ȯ
	    }
	    //            122-(1-1)*10=122,121,120,119,118,,,,
	    //            122-(2-1)*10=122-10=?
	    number=count-(currentPage-1)*pageSize;
	    System.out.println("�������� number=>"+number);

		//2.ó���� ����� �����޸𸮿� ����->�̵��� �������� �����ؼ� ���
		//request.setAttribute("Ű��",�����Ұ�)->request.getAttribute("Ű��")
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("startRow", startRow);
	    request.setAttribute("count", count);
	    request.setAttribute("pageSize", pageSize);
	    request.setAttribute("blockSize", blockSize);
	    request.setAttribute("number", number);
	    request.setAttribute("articleList", articleList);
	    
		//3.�����ؼ� �̵��� �� �ֵ��� �������� ����
		return "/list.jsp";//ControllerAction->list.jsp�� �̵�
	}
}
