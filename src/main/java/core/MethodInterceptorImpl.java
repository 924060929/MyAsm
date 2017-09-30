package core;
import java.lang.reflect.Method;

public abstract class MethodInterceptorImpl {
    public abstract Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;
}