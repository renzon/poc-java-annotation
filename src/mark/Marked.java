package mark;

public class Marked {
	@Mark
	public void marked1() {
		System.out.println("Marked 1");
	}

	@Mark
	public void marked2() {
		System.out.println("Marked 2");
	}

	public void notMarked() {
		System.out.println("Not Marked");
	}
	
	public static void main(String[] args) {
		Marked m=new Marked();
		m.marked1();
		m.marked2();
		m.notMarked();
	}
}
