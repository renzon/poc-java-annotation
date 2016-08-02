package server.examples;

import server.Route;

public class RouteExample {
	@Route(path = "/")
	public void root() {
		System.out.println("Acessing root of Example");
	}

	@Route(path = "/user")
	public void user(String username) {
		System.out.println("Acessing user of Example");
		System.out.println("Username: " + username);
	}

	public void notRouteRelated() {

	}

}
