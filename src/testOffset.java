
public class testOffset {
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder("abcdef");
		sb.insert(1, '*');
		System.out.println(sb.toString());
	}
}
