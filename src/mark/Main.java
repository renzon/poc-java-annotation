package mark;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		printMarked(Marked.class);
	}

	private static void printMarked(Class<?> cls) {
		Arrays.asList(cls.getMethods()).stream().filter(m -> m.isAnnotationPresent(Mark.class)).map(m -> m.getName())
				.forEach(System.out::println);
	}

}
