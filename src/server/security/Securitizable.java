package server.security;

import java.lang.annotation.Annotation;

public interface Securitizable {
	void check(String path, String... params) throws SecurityException;

	void extracParams(Annotation a);
}
