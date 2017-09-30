package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import utils.DescUtils;
import utils.PoolUtils;
import utils.Uuid;

/**
 * <p>Title: Enhancer</p>
 * <p>Description: Enhancer用来创建代理子类，先调用setSuperclass方法设置父类、调用setCallback方法设置回调方法拦截器后再调用create方法生成代理子类</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 上午1:11:01
 */

//TODO 为什么栈映射帧可以乱写？

@SuppressWarnings("all")
public class Enhancer extends ClassLoader implements Opcodes {
	private String superName;//父类全限定名
	private String className;//本类类名
	private String interceptorName;//方法拦截器名
	private String interceptorSetter;//方法拦截器setter名
	private MethodInterceptorImpl interceptor;//setCallback的方法拦截器
	private List<String> superMethods = new ArrayList<String>();//父类的所有方法的名字(除了Object的方法)
	private List<String> methodProxys = new ArrayList<String>();//methodProxy的名字
	private List<String> invokeSupers = new ArrayList<String>();//专门用来 调用父类方法 的方法的名字
	private List<Method> methods = new ArrayList<Method>();//父类方法
	private List<String> returnTypes = new ArrayList<String>();//各个方法的返回值类型
	private List<String> paramsDesc = new ArrayList<String>();//各个方法参数的描述符
	private List<String[]> paramClassNames = new ArrayList<String[]>();//各个方法参数类名
	private List<Integer> nParams = new ArrayList<Integer>();//各个方法参数的个数
	private int realParamNum;//实际的参数个数。由于Long、Double占两个参数位置，因此借助此变量来计算实际的参数个数
	
	/**
	 * <p>Title: setSuperclass</p>
	 * <p>Description: 设置父类方法</p>
	 * @param superclass
	 */
	public void setSuperclass(Class<?> superclass) {
		Method[] methods = superclass.getMethods();
		for (Method method : methods) {
			if (!"java.lang.Object".equals(method.getDeclaringClass().getName())) {
				this.methods.add(method);
				this.returnTypes.add(DescUtils.getDesc(method.getReturnType()));
				paramsDesc.add("(" + DescUtils.getDesc(method.getParameterTypes()) + ")");
				nParams.add(method.getParameterTypes().length);
				if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
					paramClassNames.add(DescUtils.getClassName(method.getParameterTypes()));
				} else {
					paramClassNames.add(new String[0]);
				}
			}
		}
		
		superName = superclass.getName();
		superName = superName.replaceAll("\\.", "/");
		className = superName + "_enhancer_" + Uuid.getId();//生成代理子类的名字
	}
	
	/**
	 * <p>Title: setCallback</p>
	 * <p>Description: 设置需要回调的方法拦截器</p>
	 * @param interceptor
	 */
	public void setCallback(MethodInterceptorImpl interceptor) {
		this.interceptor = interceptor;
		interceptorName = "interceptor_enhancer_" + Uuid.getId();
		interceptorSetter = "setInterceptor";
	}
	
	/**
	 * <p>Title: create</p>
	 * <p>Description: 创建代理子类，要在设置父类和设置方法拦截器之后调用</p>
	 * @return
	 */
	public Object create() {
		if (superName == null) {
			throw new IllegalStateException("没有设置父类！");
		}
		if (interceptor == null) {
			throw new IllegalStateException("没有设置方法拦截器！");
		}
		
		//jdk1.0.2之后都需要加ACC_SUPER，V1_6是指版本是1.6及以上的才能使用
		ClassHelper clazz = ClassHelper.create(V1_6, ACC_PUBLIC + ACC_SUPER, className,  superName, new String[]{"core/InterceptorSetter"});
		clazz.addField(ACC_STATIC + ACC_PRIVATE, interceptorName, "Lcore/MethodInterceptorImpl;");
		MethodHelper method = null;
		
		addStaticBlock(clazz);
		addInitAndSetter(clazz);
		for (int i = 0; i < methods.size(); ++i) {
			String superMethodName = methods.get(i).getName();
			String invokeSuper = invokeSupers.get(i);
			addInvokeSuperMethod(clazz, paramClassNames.get(i), invokeSuper, superMethodName, paramsDesc.get(i), returnTypes.get(i), nParams.get(i));
			addOverrideMethod(clazz, paramClassNames.get(i), superMethodName, superMethods.get(i), methodProxys.get(i), paramsDesc.get(i), returnTypes.get(i), nParams.get(i));
		}
		clazz.end();
		
		byte[] b = clazz.toByteArray();
		
//		try {
//			FileOutputStream out = new FileOutputStream("c://ChildProxy.class");
//			out.write(b);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		Object obj = null;
		try {
			obj = defineClass(className.replaceAll("/", "\\."), b, 0, b.length).newInstance();
			InterceptorSetter setter = ((InterceptorSetter)obj);
			setter.setInterceptor(interceptor);//注入方法拦截器
		} catch (InstantiationException | IllegalAccessException
				| ClassFormatError e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * <p>Title: putTypeClass</p>
	 * <p>Description: 将type指定的Class并放到操作数栈栈顶</p>
	 * @param method
	 * @param type
	 */
	private void putTypeClass(MethodHelper method, String type) {
		if (type.equals("int")) {
			method.field(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("char")) {
			method.field(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("byte")) {
			method.field(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("short")) {
			method.field(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("long")) {
			method.field(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("boolean")) {
			method.field(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("float")) {
			method.field(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
		} else if (type.equals("double")) {
			method.field(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
		} else {
			method.ldc(Type.getType(type));
		}
	}
	
	/**
	 * <p>Title: putSuperclass</p>
	 * <p>Description: 在代码中通过反射获取父类的方法superMethodName并保存到成员变量methodFieldName中</p>
	 * @param method
	 * @param superMethodName 将要反射获得的父类方法名
	 * @param methodFieldName 将反射得到的Method保存到此成员变量中
	 * @param types 所在方法的形参的全限定名数组
	 * @param nParam 方法的参数个数，long和double只一个
	 * @param maxStack 调用此方法之前的最大操作数栈大小
	 * @return 返回计算之后的maxStack
	 */
	private int saveSuperclass(MethodHelper method, String superMethodName, String methodFieldName, String[] types, int nParam, int maxStack) {
		method.ldc(Type.getType(className));//操作数栈为[Class(this)]
		method.invokeMethod(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;");
		method.ldc(superMethodName);//操作数栈为[Class(super), superMethodName]
		if (nParam == 0) {
			method.addCode(ACOUNST_NULL);
		} else {
			method.ldc(nParam);//操作数栈为[Class(super), superMethodName, nParam]
			method.aNewArray("java/lang/Class");//操作数栈为[Class(super), superMethodName, nParam, Class[]]
			method.var(ASTORE, 0);//操作数栈为[Class(super), superMethodName, nParam]
			for (int j = 0; j < nParam; ++j) {
				method.var(ALOAD, 0);//操作数栈为[Class(super), superMethodName, nParam, Class[]]
				method.ldc(j);//操作数栈为[Class(super), superMethodName, nParam, Class[], j]
				putTypeClass(method, types[j]);//操作数栈再加一,等于6
				method.addCode(AASTORE);//操作数减去3个,因为前三句话是本句的参数
			}
			if (nParam > 0) {
				maxStack = 6;
			}
			method.var(ALOAD, 0);
		}
		method.invokeMethod(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
		method.field(PUTSTATIC, className, methodFieldName, "Ljava/lang/reflect/Method;");
		return maxStack;
	}
	
	
	/**
	 * <p>Title: doAddStaticBlockCode</p>
	 * <p>Description: </p>
	 * @param clazz
	 * @param method
	 * @return 至少需要的操作数栈数
	 */
	/* 假如父类方法的形参个数为0，那么至少需要5个操作数栈，如果参数个数大于0，那么至少需要6个操作数栈
	 * 计算过程：
	 * clazz.addField方法各需要两个操作数栈，调用完毕后操作数为空，
	 * method.ldc(nParams.get(i))需要一个操作数，此时操作数栈为[nParams.get(i)]，
	 * method.aNewArray("java/lang/Class")需要一个操作数，但是调用此方法后，该操作数被弹出，因此maxStack至少是2
	 * method.var(ASTORE, 1)将弹出一个操作数，此时操作数栈为空，
	 * 再之后的for循环中至少需要3个操作数，maxStack至少是3，跳出for循环后操作数栈为空，
	 * method.newObj("core/MethodProxy")以及之后的4句，需要5个maxStack。因此可以提前把maxStack赋值为5。
	 */
	private int doAddStaticBlockCode(ClassHelper clazz, MethodHelper method) {
		int maxStack = 5;
		for (int i = 0; i < this.methods.size(); ++i) {
			String superMethodName = methods.get(i).getName();
			String methodFieldName = methods.get(i).getName() + "_enhancer_" + Uuid.getId();
			String invokeSuper = superMethodName + "_enhancer_invokeSuper_" + Uuid.getId();
			superMethods.add(methodFieldName);
			invokeSupers.add(invokeSuper);
			methodProxys.add("methodProxy_enhancer_" + Uuid.getId());
			
			clazz.addField(ACC_STATIC + ACC_PRIVATE, superMethods.get(i) , "Ljava/lang/reflect/Method;");
			clazz.addField(ACC_STATIC + ACC_PRIVATE, methodProxys.get(i), "Lcore/MethodProxy;");
			
			//反射获得父类的方法并保存到成员变量, 当形参个数大于0时， 应该更新maxStack为6，再此代码执行时，操作数个数为0
			maxStack = saveSuperclass(method, superMethodName, methodFieldName, paramClassNames.get(i), nParams.get(i), maxStack);

			method.ldc(nParams.get(i));
			method.aNewArray("java/lang/Class");
			method.var(ASTORE, 0);//把数组保存在局部变量表的1号位置
			for (int j = 0; j < nParams.get(i); ++j) {//把参数类型保存到一个数组中
				method.var(ALOAD, 0);//把数组放到栈顶
				method.ldc(j);//数组下标为j
				putTypeClass(method, paramClassNames.get(i)[j]);//把第i个方法的第j形参的Class放到栈顶
				method.addCode(AASTORE);//保存到数组中
			}
			
			method.newObj("core/MethodProxy");
			method.addCode(DUP);//复制上面的对象并放到栈顶
			//下面4句代表 本类.class.getMethod(superMethods, params);
			method.ldc(Type.getType(className));
			method.ldc(invokeSupers.get(i));
			method.var(ALOAD, 0);
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
			//下面两句，把父类方法作为实参去new一个MethodProxy并保存到成员变量中
			method.invokeMethod(INVOKESPECIAL, "core/MethodProxy", "<init>", "(Ljava/lang/reflect/Method;)V");
			method.field(PUTSTATIC, className, methodProxys.get(i), "Lcore/MethodProxy;");
		}
		return maxStack;
	}
	
	/**
	 * <p>Title: addStaticBlock</p>
	 * <p>Description: 添加静态代码块</p>
	 * @param clazz
	 */
	private void addStaticBlock(ClassHelper clazz) {
		MethodHelper method = null;
		method = clazz.addMethod(ACC_STATIC, "<clinit>", "()V");
		int maxStack = doAddStaticBlockCode(clazz, method);
		method.setMaxs(1, maxStack);//仅有一个Class[]类型的局部变量，保存在0号位置
		method.addCode(RETURN);
		method.end();
	}

	
	/**
	 * <p>Title: addInitAndInterceptorSetter</p>
	 * <p>Description: 增加默认构造函数和方法拦截器setter</p>
	 * @param clazz
	 * @param method
	 */
	private void addInitAndSetter(ClassHelper clazz) {
		MethodHelper method = null;
		{//添加构造方法
			method = clazz.addMethod(ACC_PUBLIC, "<init>", "()V");
			method.var(ALOAD, 0);
			method.invokeMethod(INVOKESPECIAL, superName, "<init>", "()V");
			method.addCode(RETURN);
			method.setMaxs(1, 1);
			method.end();
		}
		{//添加方法拦截器的setter
			method = clazz.addMethod(ACC_PUBLIC, interceptorSetter, "(Lcore/MethodInterceptorImpl;)V");
			method.var(ALOAD, 1);
			method.field(PUTSTATIC, className, interceptorName, "Lcore/MethodInterceptorImpl;");
			method.addCode(RETURN);
			method.setMaxs(2, 1);
			method.end();
		}
	}
	
	/**
	 * <p>Title: addInvokeSuperMethod</p>
	 * <p>Description: 增加专用用来调用父类方法的方法</p>
	 * @param clazz
	 * @param method
	 * @param types 形参的全限定名数组
	 * @param invokeSuper 专门用来调用父类方法的方法的名字
	 * @param superMethodName 父类方法的名字
	 * @param param 参数列表的描述符,包括小括号
	 * @param returnType 返回值的描述符
	 * @param nParam 形参个数
	 */
	private void addInvokeSuperMethod(ClassHelper clazz, String[] types, String invokeSuper, String superMethodName, String paramDesc, String returnType, int nParam) {
		MethodHelper method = clazz.addMethod(ACC_PUBLIC, invokeSuper, paramDesc + returnType);
		method.var(ALOAD, 0);
		int realParamNum = 1;
		for (int j = 0; j < nParam; ++j, ++realParamNum) {
			if (types[j].equals("int")) {
				method.var(ILOAD, realParamNum);
			} else if (types[j].equals("char")) {
				method.var(ILOAD, realParamNum);
			} else if (types[j].equals("byte")) {
				method.var(ILOAD, realParamNum);
			} else if (types[j].equals("short")) {
				method.var(ILOAD, realParamNum);
			} else if (types[j].equals("long")) {
				method.var(LLOAD, realParamNum++);
			} else if (types[j].equals("boolean")) {
				method.var(ILOAD, realParamNum);
			} else if (types[j].equals("float")) {
				method.var(FLOAD, realParamNum);
			} else if (types[j].equals("double")) {
				method.var(DLOAD, realParamNum++);
			} else {
				method.var(ALOAD, realParamNum);
			}
		}
		this.realParamNum = realParamNum;
		method.invokeMethod(INVOKESPECIAL, superName, superMethodName, paramDesc + returnType);
		DescUtils.addReturn(method, returnType);
		method.setMaxs(realParamNum, realParamNum);
		method.end();
	}
	
	/**
	 * <p>Title: buildTypeArray</p>
	 * <p>Description: 构建类型数组，用来作为方法拦截器的intercept方法的第三个参数</p>
	 * @param method
	 * @param types 参数的全限定名数组
	 * @param nParam 形参个数
	 */
	private void buildTypeArray(MethodHelper method, String[] types, int nParam) {
		if (nParam != 0) {
			method.ldc(nParam);
			method.aNewArray("java/lang/Object");
			method.var(ASTORE, 1 + this.realParamNum);
			for (int j = 0, realParamNum = 1; j < nParam; ++j, ++realParamNum) {
				method.var(ALOAD, 1 + this.realParamNum);
				method.ldc(j);
				if (types[j].equals("int")) {
					method.var(ILOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				} else if (types[j].equals("char")) {
					method.var(ILOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
				} else if (types[j].equals("byte")) {
					method.var(ILOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
				} else if (types[j].equals("short")) {
					method.var(ILOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
				} else if (types[j].equals("long")) {
					method.var(LLOAD, realParamNum++);
					method.invokeMethod(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
				} else if (types[j].equals("boolean")) {
					method.var(ILOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				} else if (types[j].equals("float")) {
					method.var(FLOAD, realParamNum);
					method.invokeMethod(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
				} else if (types[j].equals("double")) {
					method.var(DLOAD, realParamNum++);
					method.invokeMethod(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
				} else {
					method.var(ALOAD, realParamNum);
				}
				method.addCode(AASTORE);
			}
		}
	}
	
	/**
	 * <p>Title: invokeIntercept</p>
	 * <p>Description: 回调interceptor的intercept方法</p>
	 * @param method 父类方法的Method类
	 * @param types intercept方法的第三个参数的全限定名数组
	 * @param superMethodField 父类方法的Method类在代理子类的名字
	 * @param methodProxy 包装了父类方法的Method类的methodProxy的名字
	 * @param nParam
	 */
	private void invokeIntercept(MethodHelper method, String[] types, String superMethodField, String methodProxy, int nParam) {
		method.addCode(ACOUNST_NULL);//Class[] args = null;
		method.var(ASTORE, 1 + this.realParamNum);
		buildTypeArray(method, types, nParam);//完善参数类型数组args,args将作为方法拦截器的intercept方法的三个参数
		method.field(GETSTATIC, className, interceptorName, "Lcore/MethodInterceptorImpl;");
		method.var(ALOAD, 0);
		method.field(GETSTATIC, className, superMethodField, "Ljava/lang/reflect/Method;");
		method.var(ALOAD, 1 + this.realParamNum);
		method.field(GETSTATIC, className, methodProxy, "Lcore/MethodProxy;");
		method.invokeMethod(
				INVOKEVIRTUAL, 
				"core/MethodInterceptorImpl", 
				"intercept", 
				"(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;Lcore/MethodProxy;)Ljava/lang/Object;");
		method.var(ASTORE, 1 + nParam);
	}
	
	/**
	 * <p>Title: invokePrintStackTrace</p>
	 * <p>Description: 调用异常的printStackTrace方法</p>
	 * @param method 
	 * @param types 形参的全限定名数组
	 * @param nParam 形参的个数
	 */
	private void invokePrintStackTrace(MethodHelper method, String[] types, int nParam) {
		/**
		 * 只要局部变量表和前一个栈映射帧不同，那么栈映射帧一定是FULL_FRAME类型的，
		 * 因为栈映射帧只有FULL_FRAM类型能表示local与前一个栈映射帧不同。
		 * 在catch中，local表从[this, 形参...]变成了[this, 形参..., ret]，
		 * 操作数栈会有一个类型为catch的异常的Class,
		 * 不过不使用addFrame方法也没错，随便乱写locals和stack都没错，为什么？
		 */
		Object[] locals = new Object[3 + nParam];//设置local
		locals[0] = className;//this
		for (int i = 1; i <= nParam; ++i) {//形参
			locals[i] = types[i-1];
		}
		locals[1 + nParam] = "java/lang/Object";//Object ret
		locals[2 + nParam] = "java/lang/Throwable";
		method.addFrame(FULL_FRAME, locals, new Object[]{"java/lang/Throwable"});
		method.var(ASTORE, 2 + nParam);
		method.var(ALOAD, 2 + nParam);
		method.invokeMethod(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V");
	}
	
	/**
	 * <p>Title: overrideInit</p>
	 * <p>Description: 初始化override的方法，及Object ret = null</p>
	 * @param method
	 */
	private void overrideInit(MethodHelper method) {
		method.addCode(ACOUNST_NULL);//Object ret = null;
		method.var(ASTORE, this.realParamNum);//ret代表返回值，如果父类方法有返回值就转换这个ret并返回，否则不返回。前realParamNum的局部变量是this和方法的形参
	}
	
	/**
	 * <p>Title: overrideReturn</p>
	 * <p>Description: 处理override方法返回的返回值，以及栈映射帧</p>
	 * @param method
	 * @param returnType
	 * @param nParam
	 */
	private void overrideReturn(MethodHelper method, String returnType, int nParam) {
		//和前一个栈映射帧(默认栈映射帧)具有同样的local和stack，所以是SAME_FRAME类型的
		method.addFrame(SAME_FRAME, null, null);
		if ("V".equals(returnType)) {
			method.addCode(RETURN);
		} else {
			method.var(ALOAD, 1 + nParam);
			DescUtils.castAndReturn(method, returnType);//转换类型并返回
		}
	}
	
	/**
	 * <p>Title: addOverrideMethod</p>
	 * <p>Description: 增加重写方法</p>
	 * @param clazz
	 * @param types 形参的全限定名数组
	 * @param superMethodName 父类方法的名字
	 * @param superMethodField 父类
	 * @param methodProxy 在重写的方法中将会把此methodProxy当作实参传递给方法拦截器
	 * @param param 参数列表的描述符，包括小括号
	 * @param returnType 返回值的描述符
	 * @param nParam 形参个数
	 */
	private void addOverrideMethod(ClassHelper clazz, String[] types, String superMethodName, String superMethodField, String methodProxy, String paramDesc, String returnType, int nParam) {
		MethodHelper method = clazz.addMethod(ACC_PUBLIC, superMethodName, paramDesc + returnType);
		Label tryBegin = new Label();//Label用来表示跳转指令的目的地和try-cath的起止位置
		Label tryEnd = new Label();
		Label catchBegin = new Label();
		Label methodReturn = new Label();
		method.tryCatch(tryBegin, tryEnd, catchBegin, "java/lang/Throwable");
		
		overrideInit(method);//Object ret = null;
		
		method.addLabel(tryBegin);//调用interceptor.intercept(this, method, args, methodProxy);
		invokeIntercept(method, types, superMethodField, methodProxy, nParam);
		method.addLabel(tryEnd);
		method.jump(GOTO, methodReturn);
		
		//label和frame是成对出现的，frame在invokePrintStackTrace里
		method.addLabel(catchBegin);
		invokePrintStackTrace(method, types, nParam);
		
		method.addLabel(methodReturn);
		overrideReturn(method, returnType, nParam);
		/**
		 * 局部变量有this、ret、args和方法的形参，
		 * 调用intercept方法至少要有5个操作数，即[interceptor, this, superMethodField, args, methodProxy]
		 */
		method.setMaxs(3 + this.realParamNum, 5);
		method.end();
	}
}
