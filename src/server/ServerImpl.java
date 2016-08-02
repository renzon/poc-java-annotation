package server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServerImpl implements Server {
	private Map<String, Executor> routes = new HashMap<>();

	@Override
	public void execute(String path, String... params) {
		System.out.println("Receiving Request on path: " + path);
		if (routes.containsKey(path)) {
			routes.get(path).execute(params);
		} else {
			System.out.println("404: Page not Found");
		}
	}

	@Override
	public void scanRoutes(Class<?> cls) {
		for (Method m : cls.getMethods()) {
			try {
				String path = m.getAnnotation(Route.class).path();
				Object target = cls.newInstance();
				routes.put(path, new Executor(target, m));
			} catch (Exception e) {

			}
		}

	}

}

class Executor {
	private Object target;
	private Method method;

	public Executor(Object target, Method method) {
		super();
		this.target = target;
		this.method = method;
	}

	public void execute(String... params) {
		List<Object> list = new LinkedList<>();
		for (String s : params) {
			list.add(s);
		}
		try {
			method.invoke(target, list.toArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Errors in params");
		}
	}

}