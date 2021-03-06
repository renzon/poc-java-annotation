# poc-java-annotation
Project to Analyse Java Annotation versus Python Decorator.
Check Python counterpart in 
[poc-python-decorator](https://github.com/renzon/poc-python-decorator#poc-python-decorator).
Each following session has also links to its counterpart.


## Case 1: Annotation which does nothing

[Python Case 1: Decorator which does nothing](https://github.com/renzon/poc-python-decorator#case-1-decorator-which-does-nothing)

In fact it is in Annotation definition not doing anything. 
Its purpose is supplying code with metadata.

Let's suppose the need for marking methods and only listing their names without exection. 

### Creating Annotation Mark

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mark {}
```

It's important noticing that an Annotation `Mark` uses another Annotations to define itself. 
It must define if gonna be available at runtime (`Retention`) and that its targets are methods (`Target`).
More than been verbose, the code is kind of a paradox because `Retention` annotates itself:

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Retention { // omiting code here }
```

More than that Annotation introduce a new citizen on Java language, with new syntax and behavior.
For example it doesn't allow Inheritance like a regular Class. 

### Annotating Methods

With `Mark` done, it can be used to add metadata:

```java
public class Marked {
	@Mark
	public void marked1() {
		System.out.println("Marked 1");
	}

	@Mark
	public void marked2() {
		System.out.println("Marked 2");
	}

	public void notMarked() {
		System.out.println("Not Marked");
	}
}
```

It's important notice again that annotated methods don't do anything special.
Calling `Marked` methods has a regular output:

```java
public static void main(String[] args) {
    Marked m=new Marked();
    m.marked1();
    m.marked2();
    m.notMarked();
}

Output:

Marked 1
Marked 2
Not Marked
```

### Listing Annotated Methods

Listing the Annotated methods is possible by reading metadada through Java Reflection API.
In this example nothing will be executed. 
Annotated methods will only have their names printed:

```java
public class Main {
	public static void main(String[] args) {
		printMarked(Marked.class);
	}

	private static void printMarked(Class<?> cls) {
		Arrays.asList(cls.getMethods()).stream()
            .filter(m -> m.isAnnotationPresent(Mark.class))
            .map(m -> m.getName())
            .forEach(System.out::println);
	}
}
```

Output:

```
marked1
marked2
```

So the conclusion on this section is that Annotation and processing are two completely differently things.

[Python Case 1: Decorator which does nothing](https://github.com/renzon/poc-python-decorator#case-1-decorator-which-does-nothing)

## Case 2: Annotation which does something

[Case 2: Decorator which does something](https://github.com/renzon/poc-python-decorator#case-2-decorator-which-does-something)

A micro framework to measure methods running time can be accomplished using previous approach.
The difference in this case is the annotated methods execution.

So first the marking Annotation can be created:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timing {}
```

A class using `Timing`can be also created:

```java
public class Counter {
	@Timing
	public void count() {
		for (int i = 0; i < 1000; ++i) {
			System.out.println(i);
		}
	}
}
```

It's important noting that just like the first case, `count` methods isn't modified:

```java
public static void main(String[] args) {
    new Counter().count();
}
```

Output:
```
...
997
998
999
```
So to calculate execution time a processing class must be developed:

```java
public class TimingRunner {
	public static void time(Object obj)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m : obj.getClass().getMethods()) {
			if (m.isAnnotationPresent(Timing.class)) {
				long begin = new Date().getTime();
				m.invoke(obj);
				long end = new Date().getTime();
				System.out.println("Method " + m.getName() + " Executed in " + (end - begin) + "ms");
			}
		}
	}

	public static void main(String[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		time(new Counter());
	}
```

Now before executing `count` initial time is kept in `begin` variable. 
After that its execution elapsed time is calculate and printed:
 
```
...
997
998
999
Method count Executed in 14ms
```

[Case 2: Decorator which does something](https://github.com/renzon/poc-python-decorator#case-2-decorator-which-does-something)

# Annotation Framework

Several frameworks use Annotations as base for extension points.
So a Proof of Concept will be implemented to illustrate this.
Thus a simple version of server routing and security is going to be developed on next sections.

## Receiving parameters

[Python - Receiving parameters](https://github.com/renzon/poc-python-decorator#receiving-parameters)

Routing configuration is a common problem that every web framework must deal with.
Some of them use Annotation to configure paths to which a method should respond.
So the first step, again, is creating an Annotation.
But this time it needs defining the paths:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
	String[] value();
}
```

It's important noticing the definition of `value`.
The syntax is different from a class attribute.
It seems a method execution (`value()`), but it isn't.
Now it can be used to annotate methods:

```java
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
}
```

The difference now is that `Route` receives a parameter which is hold by `value`
Four facts worth mentioning:

1. Besides values been a String array (`String[]`) on `root` a simple string `"/"`is given as parameter.
 In one hand it makes code simpler once it isn't necessary wrapping unitary parameter as array, e.g. `{"/"}`.
 But on the other hand it violates the supposed strong Java typing.
2. An annotation attribute can have any name.
 But if one different from `value` is chosen its name can't be omitted when passing parameter. 
 For example, if `paths` was the attribute's name, then annotated methods should be:
 
 ```java
  @Route(paths="/")
  public void root() {
		System.out.println("Acessing root of Example");
  }
 ```
3. Using a var args would be more consistent in this case, but Java currently doesn't accept it on Annotations.
4. Only primitives, String, Class, Enum, Annotation or array of these types can be used as attributes.
 Thus no customized user objects allowed.

## Mapping paths to methods

Once `Router`is in place an Interface for a simple web service is defined:

```java
public interface Server {
	void execute(String path, String... params);

	void scan(Class<?> cls);

	void addSecurity(Class<? extends Annotation> cls, Class<? extends Securitizable> sec);
}
```

The first important method is `scan`.
It's purpose is scanning classes and mapping annotated methods to their paths.
An instance is created so Executor keep references to object and its method for further execution:

```java
Route routeAnnotation = m.getAnnotation(Route.class);
if (routeAnnotation != null) {
    String[] paths = routeAnnotation.value();
    try {

        Object target = cls.newInstance();
        Executor executor = new Executor(target, m);
        for (String path : paths) {
            routes.put(path, executor);
        }
    } catch (Exception e) {}
}
```
With mapped paths server can be used to delegate execution to annotated methods.
Thus Routing is the first extension point of Server's Proof of Concept:

```java
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

// Results:

Receiving Request on path: /
Acessing root of Example
Receiving Request on path: /user
Acessing user of Example
Username: Manager
Receiving Request on path: /usr
Acessing user of Example
Username: Admin
Receiving Request on path: /notexisting
404: Page not Found
```

[Python - Receiving parameters](https://github.com/renzon/poc-python-decorator#receiving-parameters)
	
## Security

[Python Security](https://github.com/renzon/poc-python-decorator#security)

Security issue differs from previous because it modifies execution flow.
Mains idea is provide an extension point so framework users can plug their own Annotations and security handlers.
So an Interface is created to handle security:

```java
public interface Securitizable {
	void check(String path, String... params) throws SecurityException;
	void extracParams(Annotation a);
}
```

Method `extractParams` is responsible for extracting possible configurations parameters from an Annotation.

Method `check` is responsible for check execution. 
If it raises `SecurityException` the execution is interrupted.
 
Finalizing `Server` interface provide a configuration method `addSecurity` to connect Annotation with respective 
`Securitizable`.
Interface is repeated here for convenience:

```java
public interface Server {
	void execute(String path, String... params);

	void scan(Class<?> cls);

	void addSecurity(Class<? extends Annotation> cls, Class<? extends Securitizable> sec);
}
```

Once classes are mapped they are used to generate security objects and adding them to `Executor` instances:

```java
private void configureSecurity(Executor executor, Annotation[] annotations)
        throws InstantiationException, IllegalAccessException {
    for (Annotation a : annotations) {
        Class<? extends Annotation> annotationType = a.annotationType();
        if (securityMap.containsKey(annotationType)) {
            Securitizable security = securityMap.get(annotationType).newInstance();
            security.extracParams(a);
            executor.add(security);
        }
    }
}
```

The execution method now includes security checking:

```java
public void execute(String path, String... params) {
    List<Object> list = new LinkedList<>();
    for (String s : params) {
        list.add(s);
    }
    try {
        for (Securitizable s : securities) {
            s.check(path, params);
        }
        // Executed only if each security doesn't throw Security Exception
        method.invoke(target, list.toArray());
    } catch (SecurityException e) {
        e.printStackTrace();
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        throw new RuntimeException("Errors in params");
    }
}
```

After all this architecture the extension point can be tested.
First `RestrictTo` Annotation is created to define groups allowed to execute a method:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestrictTo {
	String[]value();
}
```

Second the respective interface implementation:
 
```java
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
```

With those the example class can be modified to add security Annotation:

```java
public class SecurityExample {
	@Route("/")
	public void root() {
		System.out.println("Acessing root of Example");
	}

	@RestrictTo("Admin")
	@Route({ "/user", "/usr" })
	public void user(String username) {
		System.out.println("Acessing user of Example");
		System.out.println("Username: " + username);
	}
}
```

Now the complete Server example can run with security:

```java
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

// Results:

Receiving Request on path: /
Acessing root of Example
Receiving Request on path: /user
Acessing user of Example
Username: Admin
Receiving Request on path: /usr
server.security.SecurityException: User Manager cant access this path
	at server.examples.security.RestrictToSecurity.check(RestrictToSecurity.java:22)
	at server.Executor.execute(ServerImpl.java:97)
	at server.ServerImpl.execute(ServerImpl.java:24)
	at server.examples.security.SecurityMain.main(SecurityMain.java:16)
Receiving Request on path: /notexisting
404: Page not Found
```

Thus the micro framework is completing showing the use of Annotations to add extension points.
Methods are been routed and security is properly configured using this kind of metadata.

[Python Security](https://github.com/renzon/poc-python-decorator#security)

# Conclusion

To finalize, the table bellow is created to compare Python Decorator versus Java Annotation:


      Feature              |      Python Decorator            |    Java Annotation
---------------------------|----------------------------------|-----------------------------------------------
Alters method/function     | Yes                              | No, need post processing through Reflection
Uses existing language     | Yes, function                    | No, Annotation was created
Uses only Object Orient.   | No, functional programming       | Yes
Unrestricted target        | Yes                              | No, need to define method, class, attribute
Automatic Mapping          | Yes, but module must be imported | No, class scan needed 
Unrestricted params        | Yes                              | No, can't define var args and general classes
Unrestricted param types   | Yes                              | No, only primitives, Class, Enum or array of these types
Param keep code simple     | No, extra level of function      | Yes, only add attribute call
Exec. independent of order | No                               | Yes
Keep target integrity      | No, need fix with wraps          | Yes
# of methods/functions \*  | 3                                | 10
Lines of Code \*\*         | 40                               | 187

\* Counted only for framework. Discarded Java Interfaces. 

\*\* Counted only for framework. Interfaces included

The above the table is construct so "Yes" means positive and "No' negative.
So my personal conclusion is that Python Decorator is simpler than Java Annotation.
Besides that, different opinions are very welcome ;)