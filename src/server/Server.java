package server;

public interface Server {
	public void execute(String path, String... params);

	public void scanRoutes(Class<?> cls);
}
