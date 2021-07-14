package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//모델에 대한 클래스를 불러오기 위해서 import
import ch.board.*;//BoardDAO
import java.util.*;//List

public class ListAction implements CommandAction {

	// /list.do로 요청이 들어왔을때 처리해주는 메서드
	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		// TODO Auto-generated method stub
		
		//1.jsp에서 담당했던 자바코드를 전부 액션클래스로 이동
	     int pageSize=5;//numPerPage->페이지당 보여주는 게시물수(=레코드수)
	     int blockSize=3;//pagePerBlock->블럭당 보여주는 페이지수 
	    
	    //게시판을 맨 처음 실행시키면 무조건 1페이지부터 출력
	    String pageNum=request.getParameter("pageNum"); 
	    if(pageNum==null){
	    	pageNum="1";//default(무조건 1페이지는 선택하지 않아도 보여줘야 되기때문에),가장 최근의 글
	    }
	    //현재페이지(클릭해서 보고 있는 페이지)=nowPage
	    int currentPage=Integer.parseInt(pageNum);//"1"->1 
	    //메서드 호출->시작 레코드번호
	    //                   (1-1)*10+1=1, (2-1)*10+1=11, (3-1)*10+1=21
		int startRow=(currentPage-1)*pageSize+1; //시작 레코드 번호
		//                 1*10=10,2*10=20,3*10=30
	    int endRow=currentPage*pageSize;//마지막 레코드 번호
	    int count=0;//총레코드수 
	    int number=0;//beginPerPage->페이지별로 시작하는 맨 처음에 나오는 게시물번호
	    List articleList=null;//화면에 출력할 레코드를 저장할 변수(=객체)
	    
	    BoardDAO dbPro=new BoardDAO();
	    count=dbPro.getArticleCount();//select count(*) from board->member
	    System.out.println("현재 레코드수(count)=>"+count);
	    if(count > 0){
	    	//첫번째 레코드, 불러올 갯수
	    	articleList=dbPro.getArticles(startRow, pageSize);//endRow(x)
	    }else {//count=0
	    	articleList=Collections.EMPTY_LIST;//아무것도 없는 빈 list객체를 반환
	    }
	    //            122-(1-1)*10=122,121,120,119,118,,,,
	    //            122-(2-1)*10=122-10=?
	    number=count-(currentPage-1)*pageSize;
	    System.out.println("페이지별 number=>"+number);

		//2.처리한 결과를 서버메모리에 저장->이동할 페이지에 공유해서 사용
		//request.setAttribute("키명",저장할값)->request.getAttribute("키명")
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("startRow", startRow);
	    request.setAttribute("count", count);
	    request.setAttribute("pageSize", pageSize);
	    request.setAttribute("blockSize", blockSize);
	    request.setAttribute("number", number);
	    request.setAttribute("articleList", articleList);
	    
		//3.공유해서 이동할 수 있도록 페이지를 지정
		return "/list.jsp";//ControllerAction->list.jsp로 이동
	}
}
