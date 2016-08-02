package server;

import java.lang.annotation.Annotation;

import server.security.Security;

public interface Server {
	void execute(String path, String... params);

	void scan(Class<?> cls);
	
	void addSecurity(Annotation ann, Security sec );

}
