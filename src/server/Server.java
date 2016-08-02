package server;

import java.lang.annotation.Annotation;

import server.security.Securitizable;

public interface Server {
	void execute(String path, String... params);

	void scan(Class<?> cls);

	void addSecurity(Class<? extends Annotation> cls, Class<? extends Securitizable> sec);

}
