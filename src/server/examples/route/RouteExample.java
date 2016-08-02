package server.examples.route;

import server.Route;

public class RouteExample {
	@Route("/")
	public void root() {
		System.out.println("Acessing root of Example");
	}

	@Route({ "/user", "/usr" })
	public void user(String username) {
		System.out.println("Acessing user of Example");
		System.out.println("Username: " + username);
	}

	public void notRouteRelated() {

	}

}
