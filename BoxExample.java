package sec02_exam02_generic_type;

public class BoxExample {

	//���� Ŭ������ ������ �� public �� �ϰ� �Ǹ� main ���� ȣ�� ������ �ʱ� ������ ������ �߻��Ѵ�. 
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
		//box.set(new Integer()); �̹� ���׸� Ÿ���� String ���� �������ִ�. 
		box.set("ȫ�浿"); 	
		String obj = box.get(); 
		
		Box<Integer> box2 = new Box<Integer>();
		box2.set(6);
		box2.get();
 	}
}
