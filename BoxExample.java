package sec02_exam02_generic_type;

public class BoxExample {

	//내부 클래스를 선언할 때 public 만 하게 되면 main 보다 호출 순서가 늦기 때문에 에러가 발생한다. 
	public static class Box<T> {
		private T object;
		public void set(T object) {
			this.object = object;
		}
		
		public T get() {
			return object;
		}
	}
	public static void main(String[] args) {
		Box<String> box = new Box<String>();	
		//box.set(new Integer()); 이미 제네릭 타입이 String 으로 결정나있다. 
		box.set("홍길동"); 	
		String obj = box.get(); 
		
		Box<Integer> box2 = new Box<Integer>();
		box2.set(6);
		box2.get();
 	}
}
