package server.examples.route;

import server.Server;
import server.ServerImpl;

public class RouteMain {
	public static void main(String[] args) {
		Server server = new ServerImpl();
		// Scan could be done by libs, but keeping it simple
		server.scan(RouteExample.class);
		// Executing paths
		server.execute("/");
		server.execute("/user", "Manager");
		server.execute("/usr", "Admin");
		server.execute("/notexisting");
	}
}
