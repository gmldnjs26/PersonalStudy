package sec03_exam01_no_argument_no_return;

public class MyFuntionalInterfaceExample {
	public static void main(String[] args) {
		MyFuntionalInterface fi; // 함수적 인터페이스
		fi = () -> {
			String str = "method call1";
			System.out.println(str);
		};
		fi.method(); // 람다식 작성한 친구들 싱행		
		
		fi = () -> System.out.println("method call2");
		fi.method();		
		
		fi = new MyFuntionalInterface() {
			public void method() {
				System.out.println("asdasd");
			};
		};
		fi.method();
	}
	@FunctionalInterface //메소드가 하나 선언되어있는지 컴파일러가 체크
	public interface MyFuntionalInterface { // 람다식의 타겟타입이 될 interface
		public void method();  
	}
}
