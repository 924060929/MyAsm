package utils;

import core.MethodHelper;
import core.Opcodes;

/**
 * <p>Title: DescUtils</p>
 * <p>Description: 获得描述符的工具类</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午2:57:11
 */
public class DescUtils implements Opcodes {
	
	/**
	 * <p>Title: addReturn</p>
	 * <p>Description: 根据returnDesc为方法增加返回指令</p>
	 * @param method
	 * @param returnDesc
	 */
	public static void addReturn(MethodHelper method, String returnDesc) {
		if ("I".equals(returnDesc)) {
			method.addCode(IRETURN);
		} else if ("C".equals(returnDesc)) {
			method.addCode(IRETURN);
		} else if ("S".equals(returnDesc)) {
			method.addCode(IRETURN);
		} else if ("B".equals(returnDesc)) {
			method.addCode(IRETURN);
		} else if ("F".equals(returnDesc)) {
			method.addCode(FRETURN);
		} else if ("J".equals(returnDesc)) {
			method.addCode(LRETURN);
		} else if ("D".equals(returnDesc)) {
			method.addCode(DRETURN);
		} else if ("Z".equals(returnDesc)) {
			method.addCode(IRETURN);
		} else if ("V".equals(returnDesc)) {
			method.addCode(RETURN);
		} else if (returnDesc.charAt(0) == 'L' && returnDesc.charAt(returnDesc.length()-1) == ';') {
			method.addCode(ARETURN);
		} else {
			throw new IllegalArgumentException("非法的方法返回值描述符:" + returnDesc);
		}
	}
	
	/**
	 * <p>Title: castAndReturn</p>
	 * <p>Description: 增加转换代码和并根据类型增加返回代码</p>
	 * @param method
	 * @param returnDesc
	 */
	public static void castAndReturn(MethodHelper method, String returnDesc) {
		if ("I".equals(returnDesc)) {
			method.cast("java/lang/Integer");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			method.addCode(IRETURN);
		} else if ("C".equals(returnDesc)) {
			method.cast("java/lang/Character");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
			method.addCode(IRETURN);
		} else if ("S".equals(returnDesc)) {
			method.cast("java/lang/Short");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
			method.addCode(IRETURN);
		} else if ("B".equals(returnDesc)) {
			method.cast("java/lang/Byte");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
			method.addCode(IRETURN);
		} else if ("F".equals(returnDesc)) {
			method.cast("java/lang/Float");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
			method.addCode(FRETURN);
		} else if ("J".equals(returnDesc)) {
			method.cast("java/lang/Long");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
			method.addCode(LRETURN);
		} else if ("D".equals(returnDesc)) {
			method.cast("java/lang/Double");
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
			method.addCode(DRETURN);
		} else if ("Z".equals(returnDesc)) {
			method.cast("java/lang/Boolean");
			Boolean.FALSE.booleanValue();
			method.invokeMethod(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
			method.addCode(IRETURN);
		} else if ("V".equals(returnDesc)) {
			method.addCode(RETURN);
		} else if (returnDesc.charAt(0) == 'L' && returnDesc.charAt(returnDesc.length()-1) == ';') {
			method.cast(returnDesc.substring(1, returnDesc.length()-1));
			method.addCode(ARETURN);
		} else {
			throw new IllegalArgumentException("非法的方法返回值描述符:" + returnDesc);
		}
	}
	
	/**
	 * <p>Title: getDesc</p>
	 * <p>Description: 根据Class数组获得描述符</p>
	 * @param params
	 * @return
	 */
	public static String getDesc(Class<?>[] params) {
		if (params != null && params.length > 0) {
			String paramName = "";
			for (Class<?> param : params) {
				paramName += DescUtils.getDesc(param);
			}
			return paramName;
		}
		return "";
	}
	
	/**
	 * <p>Title: getDesc</p>
	 * <p>Description: 根据Class获得描述符</p>
	 * @param clazz
	 * @return
	 */
	public static String getDesc(Class<?> clazz) {
		if (int.class.equals(clazz)) {
			return "I";
		} else if (char.class.equals(clazz)) {
			return "C";
		} else if (short.class.equals(clazz)) {
			return "S";
		} else if (byte.class.equals(clazz)) {
			return "B";
		} else if (float.class.equals(clazz)) {
			return "F";
		} else if (long.class.equals(clazz)) {
			return "J";
		} else if (double.class.equals(clazz)) {
			return "D";
		} else if (boolean.class.equals(clazz)) {
			return "Z";
		} else if (void.class.equals(clazz)) {
			return "V";
		} else {
			return "L" + clazz.getName().replaceAll("\\.", "/") + ";";
		}
	}
	
	/**
	 * <p>Title: getDesc</p>
	 * <p>Description: 根据classname获得描述符</p>
	 * @param classname
	 * @return
	 */
	public static String getDesc(String classname) {
		if ("int".equals(classname)) {
			return "I";
		} else if ("char".equals(classname)) {
			return "C";
		} else if ("short".equals(classname)) {
			return "S";
		} else if ("byte".equals(classname)) {
			return "B";
		} else if ("float".equals(classname)) {
			return "F";
		} else if ("long".equals(classname)) {
			return "J";
		} else if ("double".equals(classname)) {
			return "D";
		} else if ("boolean".equals(classname)) {
			return "Z";
		} else if ("void".equals(classname)) {
			return "V";
		} else {
			return "L" + classname.replaceAll("\\.", "/") + ";";
		}
	}
	
	/**
	 * <p>Title: getClassName</p>
	 * <p>Description: 根据Class数组获得全限定名数组</p>
	 * @param params
	 * @return
	 */
	public static String[] getClassName(Class<?>[] params) {
		String[] ret = null;
		if (params != null && params.length > 0) {
			ret = new String[params.length];
			for (int i = 0; i < params.length; ++i) {
				Class<?> param = params[i];
				ret[i] = DescUtils.getClassName(param);
			}
		}
		return ret;
	}
	
	/**
	 * <p>Title: getClassName</p>
	 * <p>Description: 根据Class获得全限定名</p>
	 * @param clazz
	 * @return
	 */
	public static String getClassName(Class<?> clazz) {
		if (int.class.equals(clazz)) {
			return "int";
		} else if (char.class.equals(clazz)) {
			return "char";
		} else if (short.class.equals(clazz)) {
			return "short";
		} else if (byte.class.equals(clazz)) {
			return "byte";
		} else if (float.class.equals(clazz)) {
			return "float";
		} else if (long.class.equals(clazz)) {
			return "long";
		} else if (double.class.equals(clazz)) {
			return "double";
		} else if (boolean.class.equals(clazz)) {
			return "boolean";
		} else if (void.class.equals(clazz)) {
			return "void";
		} else {
			return clazz.getName().replaceAll("\\.", "/");
		}
	}
}
