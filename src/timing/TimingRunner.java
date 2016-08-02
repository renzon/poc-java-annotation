package timing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class TimingRunner {

	public static void time(Object obj)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m : obj.getClass().getMethods()) {
			if (m.isAnnotationPresent(Timing.class)) {
				long begin = new Date().getTime();
				m.invoke(obj);
				long end = new Date().getTime();
				System.out.println("Method " + m.getName() + " Executed in " + (end - begin) + "ms");
			}
		}
	}

	public static void main(String[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		time(new Counter());
	}

}
