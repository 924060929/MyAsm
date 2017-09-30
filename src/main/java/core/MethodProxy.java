package core;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodProxy {
	private Method superMethod; 
	public MethodProxy(Method superMethod) {
		this.superMethod = superMethod;
	}
	public Object invokeSuper(Object obj, Object... args) {
		Object ret = null;
		try {
			ret = superMethod.invoke(obj, args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
