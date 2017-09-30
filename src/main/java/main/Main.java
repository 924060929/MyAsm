package main;


import java.lang.reflect.Method;

import core.Enhancer;
import core.MethodInterceptorImpl;
import core.MethodProxy;

public class Main {
	public static void main(String[] args) throws Throwable {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Father.class);
		enhancer.setCallback(new MethodInterceptorImpl() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy methodProxy) throws Throwable {
				System.out.println("输出之前");
				Object ret = methodProxy.invokeSuper(obj, args);
				System.out.println("输出之后");
				return ret;
			}
		});
		Father father = (Father)enhancer.create();
		System.out.println(father.see(true, new I(), 1L, "haha"));
		System.out.println("\n\n分割线---------------------");
		father.a(998);
		System.out.println("\n\n分割线---------------------");
		System.out.println(father.b());
		System.out.println("\n\n分割线---------------------");
		father.what();
	}
}
