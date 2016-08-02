package timing;

public class Counter {
	@Timing
	public void count() {
		for (int i = 0; i < 1000; ++i) {
			System.out.println(i);
		}
	}
	
	public static void main(String[] args) {
		new Counter().count();
	}
}
