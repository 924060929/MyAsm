package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import attribute.AbstractAttributeInfo;
import constant.ClassInfo;
import constant.ConstantType;
import constant.Utf8Info;

/**
 * <p>Title: ClassHelper</p>
 * <p>Description: 模仿ASM框架的ClassWriter，封装了ClassImage，从而更方便地编辑一个类的字节码</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午1:15:22
 */
public class ClassHelper {
	private ClassImage image;
	private List<ConstantType> pool;
	//poolNum用来快速找到已存在的常量的下标
	private Map<String, Integer> poolNum = new HashMap<String, Integer>();
	private String className;
	private List<FieldInfo> fields = new ArrayList<FieldInfo>();//字段表
	private List<MethodInfo> methods = new ArrayList<MethodInfo>();//方法表
	private List<AbstractAttributeInfo> attrs = new ArrayList<AbstractAttributeInfo>();//属性表
	
	private ClassHelper() {}
	
	/**
	 * <p>Title: create</p>
	 * <p>Description: 使用此方法来创建ClassHelper实例</p>
	 * @param version 版本号
	 * @param access 权限
	 * @param className 类名
	 * @param extendsClass 父类名
	 * @param implNames 接口名
	 * @return
	 */
	public static ClassHelper create(int version, int access, String className, String extendsClass, String[] implNames) {
		className = className.replaceAll("\\.", "/");
		extendsClass = extendsClass.replaceAll("\\.", "/");
		if (implNames != null && implNames.length > 0) {
			for (int i = 0; i < implNames.length; ++i) {
				implNames[i] = implNames[i].replaceAll("\\.", "/");
			}
		}
		ClassHelper helper = new ClassHelper();
		helper.className = className;
		helper.image = new ClassImage();
		helper.image.setThisClass(new byte[] {0, 1});
		helper.image.setSuperClass(new byte[] {0, 3});
		helper.image.setMinorVersion(new byte[]{0, 0});
		helper.image.setMajorVersion(ByteUtils.intToBytes(version, 2));
		helper.image.setAccess(ByteUtils.intToBytes(access, 2));
		helper.setConstantPool(className, extendsClass, implNames);
		return helper;
	}
	
	/**
	 * <p>Title: read</p>
	 * <p>Description: 将字节码转换成对象树</p>
	 * @param byteClass
	 * @return
	 */
	public static ClassHelper read(byte[] byteClass) {
		ClassHelper helper = new ClassHelper();
		helper.image = new ClassImage(byteClass);
		return helper;
	}
	
	/**
	 * <p>Title: getClassImage</p>
	 * <p>Description: 当需要直接修改字节码数组时，可以通过这个方法获得对象树，然后直接设置byte值</p>
	 * @return
	 */
	public ClassImage getClassImage() {
		return image;
	}
	
	/**
	 * <p>Title: addField</p>
	 * <p>Description: 为代理子类增加一个字段</p>
	 * @param access 权限
	 * @param fieldName 字段名
	 * @param type 描述符
	 */
	public void addField(int access, String fieldName, String type) {
		int namePos = PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, fieldName);
		int typePos = PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, type);
		FieldInfo field = new FieldInfo();
		field.setAccessFlags(ByteUtils.intToBytes(access, 2));
		field.setNameIndex(ByteUtils.intToBytes(namePos, 2));
		field.setDescriptorIndex(ByteUtils.intToBytes(typePos, 2));
		fields.add(field);
	}

	/**
	 * <p>Title: addMethod</p>
	 * <p>Description: 为代理子类增加一个方法</p>
	 * @param access 权限
	 * @param name 方法名
	 * @param type 描述符
	 * @return MethodHelper
	 */
	public MethodHelper addMethod(int access, String name, String type) {
		MethodHelper method = new MethodHelper(this, className, pool, poolNum, access, name, type);
		return method;
	}
	
	/**
	 * <p>Title: setConstantPool</p>
	 * <p>Description: 设置常量池</p>
	 * @param className 类名
	 * @param extendsClass 父类名
	 * @param implNames 接口名
	 */
	private void setConstantPool(String className, String extendsClass, String[] implNames) {
		pool = new ArrayList<ConstantType>();
		pool.add(null);
		addThisAndSuper(className, extendsClass, implNames);
	}
	
	/**
	 * <p>Title: addThisAndSuper</p>
	 * <p>Description: 把构造方法和父类的构造方法需要的常量放到常量池中</p>
	 * @param className
	 * @param extendsClass
	 * @param implNames
	 */
	private void addThisAndSuper(String className, String extendsClass, String[] implNames) {
		ClassInfo thisClass = new ClassInfo();
		thisClass.setIndex(new byte[] {0, 2});
		poolNum.put("Class:" + className, pool.size());
		pool.add(thisClass);
		Utf8Info thisStr = new Utf8Info();
		thisStr.setBytes(className.getBytes());
		poolNum.put("Utf8:" + className, pool.size());
		pool.add(thisStr);
		
		ClassInfo superClass = new ClassInfo();
		superClass.setIndex(new byte[] {0, 4});
		poolNum.put("Class:" + extendsClass, pool.size());
		pool.add(superClass);
		Utf8Info superStr = new Utf8Info();
		superStr.setBytes(extendsClass.getBytes());
		poolNum.put("Utf8:" + extendsClass, pool.size());
		pool.add(superStr);
		
		if (implNames != null && implNames.length > 0) {
			byte[][] impls = new byte[implNames.length][];
			for (int i = 0; i < implNames.length; ++i) {
				String implName = implNames[i];
				ClassInfo implClass = new ClassInfo();
				implClass.setIndex(ByteUtils.intToBytes(6 + i*2, 2));
				int classPos = pool.size();
				poolNum.put("Class:" + implName, classPos);
				pool.add(implClass);
				Utf8Info implStr = new Utf8Info();
				implStr.setBytes(implName.getBytes());
				poolNum.put("Utf8:" + implName, pool.size());
				pool.add(implStr);
				impls[i] = ByteUtils.intToBytes(classPos, 2);
			}
			image.setInterfaces(impls);
		}
	}
	
	/**
	 * <p>Title: addMethod</p>
	 * <p>Description: 在调用MethodHelper的方法时会调用此方法，等到调用ClassHelper的end方法时才统一增加方法</p>
	 * @param m
	 */
	void addMethod(MethodInfo m) {
		methods.add(m);
	}
	
	/**
	 * <p>Title: end</p>
	 * <p>Description: 开始生成代理子类</p>
	 */
	public void end() {
		ConstantType[] pool = new ConstantType[this.pool.size()];
		this.pool.toArray(pool);
		this.image.setPool(pool);
		FieldInfo[] fields = new FieldInfo[this.fields.size()];
		this.fields.toArray(fields);
		image.setFields(fields);
		MethodInfo[] methods = new MethodInfo[this.methods.size()];
		this.methods.toArray(methods);
		image.setMethods(methods);
		AbstractAttributeInfo[] attrs = new AbstractAttributeInfo[this.attrs.size()];
		this.attrs.toArray(attrs);
		image.setAttributes(attrs);
	}
	
	/**
	 * <p>Title: toByteArray</p>
	 * <p>Description: 获得代理子类的字节码</p>
	 * @return
	 */
	public byte[] toByteArray() {
		return image.genBytes();
	}
}
