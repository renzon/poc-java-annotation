package server.examples.security;

import server.Route;

public class SecurityExample {
	@Route("/")
	public void root() {
		System.out.println("Acessing root of Example");
	}

	@RestrictTo("Renzo")
	@Route({ "/user", "/usr" })
	public void user(String username) {
		System.out.println("Acessing user of Example");
		System.out.println("Username: " + username);
	}

	public void notRouteRelated() {

	}

}
