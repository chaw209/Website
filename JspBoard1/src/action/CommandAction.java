package action;

//����� �ٸ����� ��û�� �޾Ƽ� ó�����ִ� �޼��带 �������� ����ϱ� ���ؼ� ����
import javax.servlet.http.*;//HttpServletRequest request, HttpServletResponse response

public interface CommandAction {
	
	//�̵��� �������� ��ο� ���������� �ʿ�=>��ȯ��(String)->������(ModelAndView)
	public String requestPro(HttpServletRequest request, HttpServletResponse response)throws Throwable;
}
