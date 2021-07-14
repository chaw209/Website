package controller;

import java.io.*;//FileInputStream
import java.util.*;//Map,Properties
import javax.servlet.*;
import javax.servlet.http.*;
//�߰�->�ٸ� ��Ű���� Ŭ������ �������̽��� ����
import action.CommandAction;

public class ControllerAction 
                       extends HttpServlet {
	
    //��ɾ�� ��ɾ� ó��Ŭ������ ������ ����
    private Map commandMap = new HashMap();
    
	//������ ����� ������ �ʱ�ȭ �۾�->������
    public void init(ServletConfig config) 
                    throws ServletException {
    	
  //��ο� �´� CommandPro.properties������ �ҷ���
    String props = config.getInitParameter("propertyConfig");
    System.out.println("�ҷ��°��="+props);
    
  //��ɾ�� ó��Ŭ������ ���������� ������
  //Properties��ü ����
    Properties pr = new Properties();
    FileInputStream f = null;//���Ϻҷ��ö� 
    
        try {
           //CommandPro.properties������ ������ �о��
        	f=new FileInputStream(props);
           
        	//������ ������ Properties�� ����
        	pr.load(f);
        	
        }catch(IOException e){
          throw new ServletException(e);
        }finally{
        if(f!=null) try{f.close();}catch(IOException ex){}	
        }
        	
     //��ü�� �ϳ��� ������ �� ��ü������ Properties
     //��ü�� ����� ��ü�� ����
     Iterator keyiter = pr.keySet().iterator();
     
     while(keyiter.hasNext()){
       //��û�� ��ɾ ���ϱ�����
       String command = (String)keyiter.next();
       System.out.println("command="+command);
       //��û�� ��ɾ�(Ű)�� �ش��ϴ� Ŭ�������� ����
       String className=pr.getProperty(command);
       System.out.println("className="+className);
       
       try{
       //�� Ŭ������ ��ü�� ���������� �޸𸮿� �ε�
       Class commandClass = Class.forName(className);
       System.out.println("commandClass="+commandClass);
       //��ûŬ������ ��ü�� ����->newInstance(); =>��ü���� �޼���
       Object commandInstance = commandClass.newInstance();
       System.out.println
              ("commandInstance="+commandInstance);
      
       //Map��ü commandMap�� ����
       commandMap.put(command, commandInstance);
       System.out.println("commandMap="+commandMap);
       
            } catch (ClassNotFoundException e) {
                throw new ServletException(e);
            } catch (InstantiationException e) {
                throw new ServletException(e);
            } catch (IllegalAccessException e) {
                throw new ServletException(e);
            }
        }//while
    }

    public void doGet(//get����� ���� �޼ҵ�
                     HttpServletRequest request, 
                     HttpServletResponse response)
    throws ServletException, IOException {
    	    requestPro(request,response);
    }

    protected void doPost(//post����� ���� �޼ҵ�
                     HttpServletRequest request, 
                     HttpServletResponse response)
    throws ServletException, IOException {
    	    requestPro(request,response);
    }

    //�ÿ����� ��û�� �м��ؼ� �ش� �۾��� ó��
    private void requestPro(HttpServletRequest request,
    		                HttpServletResponse response) 
    throws ServletException, IOException {
    	String view=null;//��û��ɾ ���� �̵��� �������� ����->/list.jsp
    	//��û ��ɾ ���� ��û��ɾ� Ŭ����(=�׼�Ŭ����)
    	/*
    	 * ListAction com=null; ListAction com=new ListAction();
    	 * WriteFormAction com=null; WriteFormAction com=new WriteFormAction();
    	 * ,,,
    	 */
    	//��ӱ��                                 //CommadAction com=new WriteFormAction();
    	CommandAction com=null;//CommadAction com=new ListAction();
    	//��� �ڽ�Ŭ������ ��ü�� �θ������� ����ȯ�� ���� �޾ƿ� �� �ִ�.
    	try {
    		//1.��û��ɾ �и�->jsp����
    		String command=request.getRequestURI();//~������Ʈ��~��û��ɾ����
    		// /JspBoard1/list.do
    		System.out.println("request.getRequestURI()="+command);
    		// /JspBoard1
    		System.out.println("request.getContextPath()="+request.getContextPath());
    		if(command.indexOf(request.getContextPath())==0) {//��ġ�Ǵ� �κ��� ã�ƶ�
    			command=command.substring(request.getContextPath().length());
    			System.out.println("�������� command="+command);// /list.do
    		}
    		//��û��ɾ�->/list.do->action.ListAction��ü�� ->requestPro()ȣ��->/list.jsp
    		com=(CommandAction)commandMap.get(command);
    		System.out.println("com="+com);//action.ListAction@�ּҰ�
    		//��û��ɾ ���� ��� �������� �̵����� ����
    		view=com.requestPro(request, response);//ListActionŬ������ �ۼ�
    		System.out.println("view="+view);// /list.jsp
    	}catch(Throwable e){
    		throw new ServletException(e);//���� ����ó���� ���� �����߻�
    	}//catch
    	//������ ��û��ɾ �ش��ϴ� view�� �����͸� ������Ű�鼭 �̵�
    	RequestDispatcher dispatcher=request.getRequestDispatcher(view);// /list.jsp
    	dispatcher.forward(request, response);//������ �����ϸ鼭 Ư�� jsp�� �̵�
    }
}

