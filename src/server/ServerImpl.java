package server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import server.security.Securitizable;
import server.security.SecurityException;

public class ServerImpl implements Server {
	private Map<String, Executor> routes = new HashMap<>();
	private Map<Class<? extends Annotation>, Class<? extends Securitizable>> securityMap = new HashMap<>();

	@Override
	public void execute(String path, String... params) {
		System.out.println("Receiving Request on path: " + path);
		if (routes.containsKey(path)) {
			routes.get(path).execute(path, params);
		} else {
			System.out.println("404: Page not Found");
		}
	}

	@Override
	public void scan(Class<?> cls) {
		for (Method m : cls.getMethods()) {
			scanRoute(cls, m);
		}
	}

	private void scanRoute(Class<?> cls, Method m) {
		Route routeAnnotation = m.getAnnotation(Route.class);

		if (routeAnnotation != null) {
			String[] paths = routeAnnotation.value();
			try {

				Object target = cls.newInstance();
				Executor executor = new Executor(target, m);
				for (String path : paths) {
					routes.put(path, executor);
				}

				configureSecurity(executor, m.getAnnotations());

			} catch (Exception e) {
			}
		}
	}

	private void configureSecurity(Executor executor, Annotation[] annotations)
			throws InstantiationException, IllegalAccessException {
		for (Annotation a : annotations) {
			Class<? extends Annotation> annotationType = a.annotationType();
			if (securityMap.containsKey(annotationType)) {
				Securitizable security = securityMap.get(annotationType).newInstance();
				security.extracParams(a);
				executor.add(security);
			}
		}
	}

	@Override
	public void addSecurity(Class<? extends Annotation> cls, Class<? extends Securitizable> sec) {
		securityMap.put(cls, sec);
	}
}

class Executor {
	private Object target;
	private Method method;
	private List<Securitizable> securities = new ArrayList<>();

	public Executor(Object target, Method method) {
		super();
		this.target = target;
		this.method = method;
	}

	public void add(Securitizable s) {
		securities.add(s);
	}

	public void execute(String path, String... params) {
		List<Object> list = new LinkedList<>();
		for (String s : params) {
			list.add(s);
		}
		try {
			for (Securitizable s : securities) {
				s.check(path, params);
			}
			// Executed only if each security doesn't throw Security Exception
			method.invoke(target, list.toArray());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Errors in params");
		}
	}
}
