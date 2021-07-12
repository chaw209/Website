package hewon;

//비지니스로직빈->웹상에서 호출할 메서드를 작성->DB연결을 시켜주는 클래스
import java.sql.*;//DB연동
import java.util.*;//자료구조 ->Vector,ArrayList,,,,

public class MemberDAO {//DBConnectionMgr의 객체 필요->has a 관계(1)

	//1.연결시킬 클래스의 객체를 멤버변수로 선언
	private DBConnectionMgr pool=null;//getConnection,freeConnection 필요
	
	//2.공통으로 접속할 필요로하는 객체를 멤버변수로 선언(DB때문에 필요)
	private Connection con=null;
	private PreparedStatement pstmt=null;//sql실행
	private ResultSet rs=null;//select 검색=>테이블형태
	private String sql="";//실행시킬 SQL구문 저장
	
	
	//2.생성자를 통해서 객체를 얻어오는 구문을 작성->has a 관계(2)
	public MemberDAO() {
		try {
			pool=DBConnectionMgr.getInstance();//DB연결->기본적인 기능
			System.out.println("pool=>"+pool);
		}catch(Exception e) {
			System.out.println("DB연결 실패=>"+e);
		}
	}
	
	//3.요구분석에 따른 메서드를 작성->회원로그인->매개변수 O 반환값 O
	//SQL문장에서 select=>반환값 O where 조건식->매개변수 2개
	//select id,passwd from member where id='nup' and passwd='1234';
	//1.LoginProcess.jsp에서 호출
	public boolean loginCheck(String id,String passwd) {
		//1.DB연결
		boolean check=false;
		//2.SQL문장 실행
		try {
			con=pool.getConnection();//만들어진 Connection 반환
			System.out.println("con=>"+con);//null
			sql="select id,passwd from member where id=? and passwd=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);//id값은 웹상에서 입력받은 값
			pstmt.setString(2, passwd);
			rs=pstmt.executeQuery();
			check=rs.next();//데이터가 존재=>true or 없으면 false
		}catch(Exception e) {
			System.out.println("loginCheck() 실행에러유발=>"+e);
		}finally { //3.DB연결해제구문
			//if(rs!=null) rs.close()~
			pool.freeConnection(con, pstmt, rs);
		}
	  return check;
	}
	//2.중복 id를 체크
	//select id from member where id='kkk';
	public boolean checkId(String id) { //IdCheck.jsp 호출
		boolean check=false;//중복id를 체크
		//1.DB연결
		try {
		//2.SQL구문
			con=pool.getConnection();//10개
			sql="select id from member where id=?";  //1)SQL구문 먼저 확인
			pstmt=con.prepareStatement(sql);           //2)NullPointerException
			pstmt.setString(1, id);                           //3)parameterIndex 오류
			rs=pstmt.executeQuery();
			check=rs.next();//데이터 존재 true or 없으면 false
		}catch(Exception e) {
			System.out.println("checkId()메서드 실행오류=>"+e);
		}finally {
		//3.DB연결해제
			pool.freeConnection(con, pstmt, rs);
		}
		return check;//IdCheck.jsp
	}
	
	//3.우편번호 검색->ZipCheck.jsp=>zipcodeRead메서드 호출
	// 상품검색,회원검색=>테이블,필드만 변경
	//select area1 from zipcode where area3 like '%미아2동%';=>반환값 String or Integer~
	//select zipcode,area1 from zipcode where area3 like '%미아2동%';=>반환값=>DTO형
	//select * from zipcode where area3 like '%미아2동%';//레코드가 여러개=>컬렉션객체
	//public String zipcodeRead(String area3)
	//public ZipcodeDTO zipcodeRead(String area3)
	public ArrayList<ZipcodeDTO> zipcodeRead(String area3){
		//레코드 한개이상 담을 변수(객체)선언->필드별로 저장된 DTO여러개 저장
		ArrayList<ZipcodeDTO> zipList=new ArrayList();
		
		try {
			con=pool.getConnection();
			//select * from zipcode where area3 like '미아2동%';
			sql="select * from zipcode where area3 like '"+area3+"%'";
			System.out.println("zipcodeRead의 sql=>"+sql);
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			//검색된 레코드의 값을 필드별로 담는 소스->찾은 레코드가 1개->if(rs.next())
			//한개이상==>while(rs.next())=>정확히 갯수를 모를때 사용
			while(rs.next()) {
				ZipcodeDTO tempZipcode=new ZipcodeDTO();
				tempZipcode.setZipcode(rs.getString("zipcode"));//찾은값을 Setter메서드 순서로 저장
				tempZipcode.setArea1(rs.getString("area1"));//"서울시"
				tempZipcode.setArea2(rs.getString("area2"));
				tempZipcode.setArea3(rs.getString("area3"));
				tempZipcode.setArea4(rs.getString("area4"));
				//ArrayList에 담는 구문을 담는 구문
				zipList.add(tempZipcode);
			}
		}catch(Exception e) {
			System.out.println("zipcodeRead()에러유발=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return zipList;//14개의 레코드=>ZipCheck.jsp에서 출력
	}
	
	//회원가입,회원수정,회원탈퇴
	//4.회원가입->insert into member values(?,?,?,,,,);->executeUpdate()
	//sql구문이 제대로 실행이 되는 지 확인->반환값을 사용(1 or 0) =>int =>boolean
	public boolean memberInsert(MemberDTO mem) { //(String mem_id,String,,,,8개)
	      boolean check=false;//회원가입 성공유무                //setScan(Scanner sc)
	      
	      try {
	    	  con=pool.getConnection();
	    	  System.out.println("memberInsert메서드의 con=>"+con);
	    	  //트랜잭션->오라클의 필수(대용량)->안전장치(rollback)
	    	  con.setAutoCommit(false);//default->
	    	  //commit()을 사용해야 실질적으로 데이터 저장시키겠다.
	    	  sql="insert into member values(?,?,?,?,?,?,?,?)";
	    	  pstmt=con.prepareStatement(sql);
	    	  System.out.println("pstmt=>"+pstmt);
	    	  System.out.println("mem.getMem_id()=>"+mem.getMem_id());//null
	    	  pstmt.setString(1, mem.getMem_id());//~.getMem_name())
	    	  pstmt.setString(2, mem.getMem_passwd());
	    	  pstmt.setString(3, mem.getMem_name());
	    	  pstmt.setString(4, mem.getMem_email());
	    	  pstmt.setString(5, mem.getMem_phone());
	    	  pstmt.setString(6, mem.getMem_zipcode());
	    	  pstmt.setString(7, mem.getMem_address());
	    	  pstmt.setString(8, mem.getMem_job());//~getMem_name())
	    	  int insert=pstmt.executeUpdate();//반환 1(성공), 0 (실패)
	    	  System.out.println("insert(데이터 입력유무)=>"+insert);
	    	  con.commit();//실질적으로 메모리상의 insert=>테이블에 반영된다.
	    	  if(insert > 0) {//if(insert==1){
	    		  check=true;//데이터 성공확인
	    	  }
	      }catch(Exception e) {
	    	  System.out.println("memberInsert() 에러유발=>"+e);
	      }finally {
	    	  pool.freeConnection(con, pstmt);//rs X =>select구문이 아니기에
	      }
	    return check;//1 or 0
	}
	//-------------------------------------------------------------------------
	//5.회원수정할 데이터를 웹에 출력시켜주는 메서드->특정회원 찾기(nup)
	//MemberUpdate.jsp에서 호출할 메서드를 작성
	//select * from member where id='nup';//pk
	public MemberDTO getMember(String mem_id) {
		MemberDTO mem=null;//id값에 해당되는 레코드 한개를 저장
		
		try {
			con=pool.getConnection();
			sql="select * from member where id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			rs=pstmt.executeQuery();
			//검색된 레코드의 값을 필드별로 담는 소스->찾은 레코드가 1개->if(rs.next())
			//한개이상==>while(rs.next())=>정확히 갯수를 모를때 사용
		    if(rs.next()) {
				mem=new MemberDTO();//데이터를 담아서 웹에 출력
				mem.setMem_id(rs.getString("id"));//<%=mem.getMem_id()%>
				mem.setMem_passwd(rs.getString("passwd"));
				mem.setMem_name(rs.getString("name"));
				mem.setMem_phone(rs.getString("phone"));
				mem.setMem_zipcode(rs.getString("zipcode"));
				mem.setMem_address(rs.getString("address"));
				mem.setMem_email(rs.getString("e_mail"));
				mem.setMem_job(rs.getString("job"));
			}
		}catch(Exception e) {
			System.out.println("getMember()에러유발=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return mem;
	}
	
	//6.회원수정시켜주는 메서드->회원가입와 기능이 같다.=>저장이 된다는 부분(공통)
	public boolean memberUpdate(MemberDTO mem) {
		
		boolean check=false;//회원수정 성공유무               
	      
	      try {
	    	  con=pool.getConnection();
	    	  //트랜잭션->오라클의 필수(대용량)->안전장치(rollback)
	    	  con.setAutoCommit(false);//default->
	    	  //commit()을 사용해야 실질적으로 데이터 저장시키겠다.
	    	  sql="update member set passwd=?, name=?, e_mail=?, phone=?, "
	    			 +"zipcode=?, address=?, job=? where id=?";
	    	  
	    	  pstmt=con.prepareStatement(sql);

	    	  pstmt.setString(1, mem.getMem_passwd());
	    	  pstmt.setString(2, mem.getMem_name());
	    	  pstmt.setString(3, mem.getMem_email());
	    	  pstmt.setString(4, mem.getMem_phone());
	    	  pstmt.setString(5, mem.getMem_zipcode());
	    	  pstmt.setString(6, mem.getMem_address());
	    	  pstmt.setString(7, mem.getMem_job());
	    	  pstmt.setString(8, mem.getMem_id());//id에 맞는 데이터를 찾기위해서
	    	  
	    	  int update=pstmt.executeUpdate();//반환 1(성공), 0 (실패)
	    	  System.out.println("update(데이터 수정유무)=>"+update);
	    	  con.commit();//실질적으로 메모리상의 update=>테이블에 반영된다.
	    	  if(update==1) {
	    		  check=true;//데이터 수정성공확인
	    	  }
	      }catch(Exception e) {
	    	  System.out.println("memberUpdate() 에러유발=>"+e);
	      }finally {
	    	  pool.freeConnection(con, pstmt);//rs X =>select구문이 아니기에
	      }
	    return check;//1 or 0
	}
	
	//7.회원탈퇴시켜주는 메서드
	//select passwd from member where id='nup'
	//delete from member where id='nup'
	public int memberDelete(String id,String passwd) { //1.입력X  2.입력 O
		String dbpasswd="";//DB상에서 찾은 암호를 저장
		int x=-1;//회원탈퇴 유무
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);//트랜잭션처리=>잘못 삭제한 경우 되돌리기 위해
			//1.id값에 해당하는 암호를 먼저 찾기
			pstmt=con.prepareStatement("select passwd from member where id=?");
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			//암호가 존재한다면
			if(rs.next()) {
				dbpasswd=rs.getString("passwd");
				System.out.println("dbpasswd=>"+dbpasswd);//암호를 출력
				//dbpasswd(DB상에 저장된 암호)==passwd(웹상에서 입력한 값)
				if(dbpasswd.equals(passwd)) {
					pstmt=con.prepareStatement("delete from member where id=?");
					pstmt.setString(1, id);
					int delete=pstmt.executeUpdate();
					System.out.println("delete(회원탈퇴 성공유무)=>"+delete);//1
					con.commit();
					x=1;//회원탈퇴성공
				}else{
					x=0;//회원탈퇴 실패->암호가 틀려서
				}
			}else{
				x=-1;//암호가 존재하지 않은 경우
			}
		}catch(Exception e) {
			System.out.println("memberDelete() 오류=>"+e);
		}finally {
	        pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
}







