package main;



public class Father {
	public boolean see(boolean b, I i, long l, String s) {
		Object[] a = new Object[3];
		a[0] = new Object();
		a[1] = new Object();
		System.out.println("see (" + i + ", " + b + ", " + s + ")");
		return b;
	}
	public void a(int c) {
		System.out.println("a(" + c + ")");
	}
	
	public String b() {
		System.out.println("b()");
		return "这是b()的一个神奇的返回值";
	}
	public void what() {
		System.out.println("what()");
	}
}
