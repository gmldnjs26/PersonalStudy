package sec03_exam01_no_argument_no_return;

public class MyFuntionalInterfaceExample {
	public static void main(String[] args) {
		MyFuntionalInterface fi; // �Լ��� �������̽�
		fi = () -> {
			String str = "method call1";
			System.out.println(str);
		};
		fi.method(); // ���ٽ� �ۼ��� ģ���� ����		
		
		fi = () -> System.out.println("method call2");
		fi.method();		
		
		fi = new MyFuntionalInterface() {
			public void method() {
				System.out.println("asdasd");
			};
		};
		fi.method();
	}
	@FunctionalInterface //�޼ҵ尡 �ϳ� ����Ǿ��ִ��� �����Ϸ��� üũ
	public interface MyFuntionalInterface { // ���ٽ��� Ÿ��Ÿ���� �� interface
		public void method();  
	}
}
