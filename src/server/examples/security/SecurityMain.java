package server.examples.security;

import server.Server;
import server.ServerImpl;

public class SecurityMain {

	public static void main(String[] args) {
		Server server = new ServerImpl();
		// Scan could be done by libs, but keeping it simple
		server.addSecurity(RestrictTo.class, RestrictToSecurity.class);
		server.scan(SecurityExample.class);
		// Executing paths
		server.execute("/");
		server.execute("/user", "Renzo");
		server.execute("/usr", "Eduardo");
		server.execute("/notexisting");

	}

}
