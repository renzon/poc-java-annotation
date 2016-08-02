package server.examples.security;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import server.security.Securitizable;
import server.security.SecurityException;

public class RestrictToSecurity implements Securitizable {
	private Set<String> allowedUsers = new HashSet<>();

	@Override
	public void check(String path, String... params) throws SecurityException {
		if (params.length == 0) {
			throw new SecurityException("No defined user");
		}
		String user = params[0];

		if (!allowedUsers.contains(user)) {
			throw new SecurityException("User " + user + " cant access this path");
		}
	}

	@Override
	public void extracParams(Annotation a) {
		allowedUsers.addAll(Arrays.asList(((RestrictTo) a).value()));

	}

}
