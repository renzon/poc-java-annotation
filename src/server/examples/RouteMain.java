package server.examples;

import server.Server;
import server.ServerImpl;

public class RouteMain {

	public static void main(String[] args) {
		Server server = new ServerImpl();
		// Scan could be done by libs, but keeping it simple
		server.scanRoutes(RouteExample.class);
		// Executing paths
		server.execute("/");
		server.execute("/user", "Renzo");
		server.execute("/user", "Eduardo");
		server.execute("/notexisting");

	}

}
