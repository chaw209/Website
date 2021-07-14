package ch.board;

//DBConnectionMgr(DB접속, 관리),BoardDTO(데이터를 담는 역할,반환형)
//웹상에서 호출할 메서드를 작성
import java.sql.*;//DB연결
import java.util.*;//ArrayList,List을 사용

import ch.board.BoardDTO;

public class BoardDAO {//MemberDAO(ArticleDao클래스)
	
	private DBConnectionMgr pool=null;//1.연결할 객체를 선언
	//공통으로 사용되는 객체를 멤버변수로 선언
	private Connection con=null;
	private PreparedStatement pstmt=null;//실행속도가 빠르다.
	private ResultSet rs=null;//select 구문을 사용->검색할때 필요
	private String sql="";//실행시킬 SQL구문 저장
	
	//2.생성자를 통해서 연결
	public BoardDAO () {
		try {
			pool=DBConnectionMgr.getInstance();
			System.out.println("BoardDAO의 pool="+pool);
		}catch(Exception e){
			System.out.println("DB접속 오류="+e);//계정명,암호,포트번호 확인
		}
	}//생성자
	//1. 페이징 처리를 하기 위해서 메서드 2개->총 레코드수를 구해와야한다.
	//select count(*) from board
	//selectCount()
	public int getArticleCount() { //MemberDAO->getMemberCount()
		
		int x=0;//총 레코드수를 저장
		
		try {
			con=pool.getConnection();
			System.out.println("con="+con);//디버깅
			sql="select count(*) from board";//select count(*) from member;
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {//보여주는 결과가 있다면
				x=rs.getInt(1);//변수명=rs.get자료형(필드명 또는 인덱스 번호)
				//필드명이 존재하지 않기 때문에 인덱스 번호를 사용해야 한다.(그룹함수)				
			}
		}catch(Exception e) {
			System.out.println("getArticleCount() 에러유발="+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
	
	//2.글목록보기에 해당하는 메서드 필요=>회원리스트와 동일(범위를 지정)
	//범위 연산자를 이용->limit ?,?
	//public List<BoardDTO> getArticles(int start,int end)
	//startRow(레코드 첫번째번호) int size(화면에 출력할 갯수)
	public List getArticles(int start,int end) {//getMemberList(int start,int end)
		
		List articleList=null;//ArrayList articleList=null; 레코드 10개를 기준
		
		try {
			con=pool.getConnection();
			/* 그룹번호가 가장 최신의 글을 중심으로 정렬하되, 만약에 level이 같은 그룹이 있는경우
				step값으로 오름차순을 통해서 몇번째 레코드번호부터 기준해서 몇번째 레코드까지 정렬
				 
				start=>레코드의 시작번호
				end=>레코드의 끝번호x, 불러올 레코드의 갯수 */
			sql="select * from board order by ref desc,re_step asc limit ?,?";//1~10
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, start-1);//mysql은 레코드 순번이 내부적으로 0부터 시작
			pstmt.setInt(2, end);
			rs=pstmt.executeQuery();
			//글목록보기=>회원리스트 보기
			if(rs.next()) {//레코드가 최소 만족 1개 이상 존재한다면
				articleList=new ArrayList(end);//10=>end값 갯수만큼 데이터를 저장할 공간을 생성(고정배열)
				//누적개념이 도입(벽돌쌓기)
				do {
					BoardDTO article=makeArticleFromResult();//p647 convertArticle() 동일한 기
					/*
					 * BoardDTO article=new BoardDTO();//MemberDTO mem=new MemberDTO();
					 * article.setNum(rs.getInt("num")); article.setWriter(rs.getString("writer"));
					 * article.setEmail(rs.getString("email"));
					 * article.setSubject(rs.getString("subject"));
					 * article.setPasswd(rs.getString("passwd"));
					 * 
					 * article.setReg_date(rs.getTimestamp("reg_date"));//작성날짜(now())
					 * article.setReadcount(rs.getInt("readcount"));//조회수
					 * article.setRef(rs.getInt("ref"));//그룹번호->신규글과 답변글을 묶어주는 역할
					 * article.setRe_step(rs.getInt("re_step"));//답변글이 나오는 순서(0, 1, 2 오름차순)
					 * article.setRe_level(rs.getInt("re_level"));//들여쓰기(답변의 깊이)
					 * 
					 * article.setContent(rs.getString("content"));
					 * article.setIp(rs.getString("ip"));
					 */
					//추가
					articleList.add(article);//문장을 안쓰면 null이 들어감
				}while(rs.next());
			}
		}catch(Exception e) {
			System.out.println("getArticles() 에러유발="+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return articleList;//list.jsp에서 출력
	}
	
	//3.게시판의 글쓰기 및 답변글 쓰기
    //insert into board values(?,?,,,,)
    public void insertArticle(BoardDTO article) {//~ (MemberDTO mem){
    	
    	//1.article=>신규글 인지 답변글인지 확인
    	int num=article.getNum();//0(신규글) 0이아닌 경우(답변글)
    	int ref=article.getRef();
    	int re_step=article.getRe_step();
    	int re_level=article.getRe_level();
    	//테이블에 입력할 게시물번호를 저장할 변수
    	int number=0;
    	System.out.println("insertArticle()의 내부 num=>"+num);
    	System.out.println("ref=>"+ref+",re_step=>"+re_step+",re_level=>"+re_level);
    	
    	try {
    		con=pool.getConnection();
    		//데이터를 넣어줄때 필요로하는 게시물번호가 필요
    		sql="select max(num) from board";
    		pstmt=con.prepareStatement(sql);
    		rs=pstmt.executeQuery();
    		//데이터 유무체크
    		if(rs.next()) {//보여주는 결과가 있다면 ->rs.last()->rs.getRow();
    			number=rs.getInt(1)+1;
    		}else {//현재 테이블에 데이터가 한개도 없는 경우 (0)
    			number=1;
    		}
    		//답변글이라면
    		if(num!=0) {//양수이면서 1이상
    			//같은 그룹번호를 가지고 있으면서 나(중간에 끼어들어가는 게시물)보다 큰 게시물을 찾아서
    			//그 step값을 증가시켜라
    			sql="update board set re_step=re_step+1 where ref=? and re_step > ?";
    			pstmt=con.prepareStatement(sql);
    			pstmt.setInt(1, ref);
    			pstmt.setInt(2, re_step);
    			int update=pstmt.executeUpdate();
    			System.out.println("댓글수정유무(update)=>"+update);//1 성공
    			//답변글
    			re_step=re_step+1;
    			re_level=re_level+1;
    		}else {//신규글이라면 num=0(writeForm.jsp)
    			ref=number;//1,2,3,4,5
    			re_step=0;
    			re_level=0;
    		}
    		//12개->num,reg_date,readcount(생략)=>default
    		//작성날짜->sysdate,now()을 사용하면 된다.
    		sql="insert into board(writer,email,subject,passwd,reg_date,";
    		sql+=" ref,re_step,re_level,content,ip)values(?,?,?,?,?,?,?,?,?,?)";
    		//~ (?,?,?,?,now(),?,?,?,?,?)
    		pstmt=con.prepareStatement(sql);
    		pstmt.setString(1, article.getWriter());//웹에서는 Setter로 저장된 상태
    		pstmt.setString(2, article.getEmail());
    		pstmt.setString(3, article.getSubject());
    		pstmt.setString(4, article.getPasswd());
    		pstmt.setTimestamp(5, article.getReg_date());//대신에 now()를 이용할수도 있다.
    		//---------------ref,re_step,re_level에 대한 계산이 적용이 된 상태에서 저장
    		pstmt.setInt(6, ref);//pstmt.setInt(6, article.getRef());
    		pstmt.setInt(7, re_step);
    		pstmt.setInt(8, re_level);
    		//--------------------------------------------------------------------
    		pstmt.setString(9, article.getContent());
    		pstmt.setString(10, article.getIp());//request.getRemoteAddr()
    		int insert=pstmt.executeUpdate();
    		System.out.println("게시판의 글쓰기 성공유무(insert)=>"+insert);
    	}catch(Exception e) {
    		System.out.println("insertArticle() 메서드 에러유발=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    }
    //------글상세보기--------------------------------------------------------------------
    /*
     * <a href="content.jsp?num=<%=article.getNum()%>&pageNum=<%=currentPage%>">
           <%=article.getSubject() %></a> 
     */
    //형식) select * from board where num=3
    //형식) update board set readcount=readcount+1 where num=3
    public BoardDTO getArticle(int num) {
    	
    	BoardDTO article=null;//ArrayList articleList=null;
    	
    	try {
    		con=pool.getConnection();
    		/*
    		 1.조회수 증가
    		 2.데이터를 담기
    		 */
    		sql="update board set readcount=readcount+1 where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);//mysql은 레코드수번이 내부적으로 0부터 시작
    		int update=pstmt.executeUpdate();
    		System.out.println("조회수 증가유무(update)=>"+update);
    		
    		sql="select * from board where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);
    		rs=pstmt.executeQuery();
    		
    		//수정된 레코드를 찾았다면
    		if(rs.next()) {//레코드가 최소 만족 1개이상 존재한다면
    			
    			article=makeArticleFromResult();
				/*
				 * article=new BoardDTO();//MemberDTO mem=new MemberDTO()
				 * article.setNum(rs.getInt("num")); article.setWriter(rs.getString("writer"));
				 * article.setEmail(rs.getString("email"));
				 * article.setSubject(rs.getString("subject"));
				 * article.setPasswd(rs.getString("passwd"));
				 * 
				 * article.setReg_date(rs.getTimestamp("reg_date"));//작성날짜(now())
				 * article.setReadcount(rs.getInt("readcount"));//조회수
				 * article.setRef(rs.getInt("ref"));//그룹번호->신규글과 답변글묶어주는 역할
				 * article.setRe_step(rs.getInt("re_step"));//답변글이 나오는 순서(0,1,2 오름차순)
				 * article.setRe_level(rs.getInt("re_level"));//들여쓰기(답변의 깊이)
				 * 
				 * article.setContent(rs.getString("content"));
				 * article.setIp(rs.getString("ip"));
				 */
    		}
    	}catch(Exception e) {
    		System.out.println("getArticle() 메서드 에러유발=>"+e);
    	}finally {
    	    pool.freeConnection(con, pstmt,rs);
    	}
    	return article;//list.jsp에서 출력
    }
    
    //---------중복된 레코드 한개를 담을 수 있는 따로 메서드를 작성
    private BoardDTO makeArticleFromResult() throws Exception {
    	
    	BoardDTO article=new BoardDTO();//MemberDTO mem=new MemberDTO()
		article.setNum(rs.getInt("num")); 
		article.setWriter(rs.getString("writer"));
		article.setEmail(rs.getString("email"));
		article.setSubject(rs.getString("subject"));
		article.setPasswd(rs.getString("passwd"));
		
		article.setReg_date(rs.getTimestamp("reg_date"));//작성날짜(now())
		article.setReadcount(rs.getInt("readcount"));//조회수
		article.setRef(rs.getInt("ref"));//그룹번호->신규글과 답변글묶어주는 역할
		article.setRe_step(rs.getInt("re_step"));//답변글이 나오는 순서(0,1,2 오름차순)
		article.setRe_level(rs.getInt("re_level"));//들여쓰기(답변의 깊이)
		  
		article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));
		return article;
    }
    
    //4. 글수정
    //1)수정할 데이터를 찾아서 화면에 출력시켜주는 역할->updateForm.jsp (writeForm.jsp)
    //select * from board where num=3
    public BoardDTO updateGetArticle(int num) {
    	
    	BoardDTO article=null;//ArrayList articleList=null;
    	
    	try {
    		con=pool.getConnection(); 		
    		sql="select * from board where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);
    		rs=pstmt.executeQuery();
    		
    		//레코드를 찾았다면
    		if(rs.next()) {//레코드가 존재한다면		
    			article=makeArticleFromResult();
    		}
    	}catch(Exception e) {
    		System.out.println("updateGetArticle() 메서드 에러유발=>"+e);
    	}finally {
    	    pool.freeConnection(con, pstmt,rs);
    	}
    	return article;//list.jsp에서 출력
    }
    
    //2)수정시켜주는 메서드 작성->updatePro.jsp<->writePro.jsp
    public int updateArticle(BoardDTO article) {//insertArticle
    	
    	String dbpasswd=null;//db에서 찾은 암호를 저장
    	int x=-1;//게시물의 수정 성공유무
    	
    	try {
    		con=pool.getConnection();
    		sql="select passwd from board where num=?";
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, article.getNum());
    		rs=pstmt.executeQuery();
    		
    		if(rs.next()) {//보여주는 결과가 있다면
    			dbpasswd=rs.getString("passwd");
    			System.out.println("dbpasswd="+dbpasswd);//나중에 지워야할 부분
    			
    			if(dbpasswd.contentEquals(article.getPasswd())) {
		    		sql="update board set writer=?,email=?,subject=?,passwd=?,content=? where num=?";
		    		//
		    		pstmt=con.prepareStatement(sql);
		    		pstmt.setString(1, article.getWriter());//웹에서는 Setter로 저장된 상태
		    		pstmt.setString(2, article.getEmail());
		    		pstmt.setString(3, article.getSubject());
		    		pstmt.setString(4, article.getPasswd());
		    		pstmt.setString(5, article.getContent());
		    		pstmt.setInt(6, article.getNum());
		    		int update=pstmt.executeUpdate();
		    		System.out.println("게시판의 글수정 성공유무(update)=>"+update);
		    		x=1;//수정 성공표시
    			}else{//암호가 틀린경우
    				x=0;//수정실패
    			}
    		}
    	}catch(Exception e) {
    		System.out.println("updateArticle() 메서드 에러유발=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return x;
    }
    
    //3)삭제시켜주는 메서드 작성=>수정시켜주는 메서드와 동일
    //delete from board where num=3
    public int deleteArticle(int num,String passwd) {
    	
    	String dbpasswd=null;//db에서 찾은 암호를 저장
    	int x=-1;//게시물의 삭제 성공유무
    	
    	try {
    		con=pool.getConnection();
    		sql="select passwd from board where num=?";
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);
    		rs=pstmt.executeQuery();
    		
    		if(rs.next()) {//보여주는 결과가 있다면
    			dbpasswd=rs.getString("passwd");
    			System.out.println("dbpasswd="+dbpasswd);//나중에 지워야할 부분
    			
    			if(dbpasswd.contentEquals(passwd)) {
		    		sql="delete from board where num=?";
		    		//
		    		pstmt=con.prepareStatement(sql);
		    		pstmt.setInt(1, num);
		    		int delete=pstmt.executeUpdate();
		    		System.out.println("게시판의 글삭제 성공유무(delete)=>"+delete);
		    		x=1;//삭제 성공표시
    			}else{//암호가 틀린경우
    				x=0;//삭제 실패
    			}
    		}
    	}catch(Exception e) {
    		System.out.println("deleteArticle() 메서드 에러유발=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return x;
    }
}
