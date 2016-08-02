package server.examples.security;

import server.Server;
import server.ServerImpl;

public class SecurityMain {
	public static void main(String[] args) {
		Server server = new ServerImpl();
		// Configuring security
		server.addSecurity(RestrictTo.class, RestrictToSecurity.class);
		// Scan could be done by libs, but keeping it simple
		server.scan(SecurityExample.class);
		// Executing paths
		server.execute("/");
		server.execute("/user", "Admin");
		server.execute("/usr", "Manager");
		server.execute("/notexisting");
	}
}
