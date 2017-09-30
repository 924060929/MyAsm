package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import utils.ByteUtils;
import attribute.AbstractAttributeInfo;
import attribute.AttributeInfoFactory;
import attribute.CodeAttr;
import attribute.LineNumberInfo;
import attribute.LineNumberTableAttr;
import constant.ConstantType;
import constant.ConstantTypeFactory;
import constant.DoubleInfo;
import constant.LongInfo;
import constant.Utf8Info;

/**
 * <p>Title: ClassImage</p>
 * <p>Description: 本类包装了字节码，包括常量池、字段表、方法表、等数据</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2015年1月12日 下午3:59:35
 */
@SuppressWarnings("all")
public class ClassImage {
	//魔数
	private static final byte[] MAGIC_NUMBER = {(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE};
	//暂时没用
	private byte[] originalClass;
	//字节码
	private byte[] byteClass;
	//常量池开始的位置
	private static final int POOL_OFFSET = Constant.U8;
	//常量池的包装类
	private ConstantType[] pool;
	//次版本
	private byte[] minorVersion;
	//主版本
	private byte[] majorVersion;
	//类修饰符
	private byte[] access;
	//类名的指针
	private byte[] thisClass;
	//父类名的指针
	private byte[] superClass;
	//接口数组
	private byte[][] interfaces;
	//字段表
	private FieldInfo[] fields;
	//方法表
	private MethodInfo[] methods;
	//属性表
	private AbstractAttributeInfo[] attributes;
	
	public ClassImage() {}
	
	public ClassImage(byte[] byteClass) {
		this.byteClass = byteClass;
		
		byte[] magicNumber = ByteUtils.subBytes(byteClass, 0, Constant.U4);
		if (!ByteUtils.equals(magicNumber, MAGIC_NUMBER)) {
			throw new RuntimeException("非法的byte数组: 没有以CAFEBABE开始");
		}
		
		originalClass = byteClass;
		
		this.minorVersion = ByteUtils.subBytes(byteClass, Constant.U4, Constant.U2);
		this.majorVersion = ByteUtils.subBytes(byteClass, Constant.U4 + Constant.U2, Constant.U2);
		
		int begin = resolvePool();
		this.access = ByteUtils.subBytes(byteClass, begin, Constant.U2); 
		begin += Constant.U2;
		this.thisClass = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		this.superClass = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;

		begin = resolveInterfaces(begin);
		begin = resolveFields(begin);
		begin = resolveMethods(begin);
		begin = resolveAttrs(begin);

		if (begin != byteClass.length) {
			throw new RuntimeException("非法的byte数组: 数组后" + (byteClass.length - begin) + "是多余的");
		}
	}
	
	/**
	 * <p>Title: minorVersion</p>
	 * <p>Description: 获得次版本号</p>
	 * @return
	 */
	public byte[] minorVersion() {
		return minorVersion;
	}
	
	/**
	 * <p>Title: majorVersion</p>
	 * <p>Description: 获得主版本号</p>
	 * @return
	 */
	public byte[] majorVersion() {
		return majorVersion;
	}
	
	/**
	 * <p>Title: poolSize</p>
	 * <p>Description: 常量池大小</p>
	 * @return
	 */
	public int poolSize() {
		if (pool == null) {
			return 0;
		}
		return pool.length;
	}
	
	/**
	 * <p>Title: resolvePool</p>
	 * <p>Description: 解析常量池</p>
	 * @return
	 */
	private int resolvePool() {
		int poolSize = ByteUtils.bytesToInt(byteClass, POOL_OFFSET, Constant.U2);
		pool = new ConstantType[poolSize];
		int begin = POOL_OFFSET + Constant.U2;
		for (int i = 1; i < pool.length; ++i) {
			byte tag = byteClass[begin++];
			ConstantType c = ConstantTypeFactory.build(tag);
			begin = c.read(byteClass, begin);
			if (c instanceof Utf8Info && ((Utf8Info) c).bytesLength() != 0) {
				System.out.println("#" + i + " = Utf8\t\t" + new String(((Utf8Info)c).getBytes()));
			}
			pool[i] = c;
			if (c instanceof LongInfo || c instanceof DoubleInfo) {
				++i;
			}
		}
		return begin;
	}


	/**
	 * <p>Title: resolveInterfaces</p>
	 * <p>Description: 解析接口</p>
	 * @param begin
	 * @return
	 */
	private int resolveInterfaces(int begin) {
		int interfaceNum = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		interfaces = new byte[interfaceNum][];
		for (int i = 0; i < interfaces.length; ++i) {
			interfaces[i] = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			System.out.println("interface: " + ByteUtils.bytesToInt(interfaces[i]));
		}
		return begin;
	}
	
	
	/**
	 * <p>Title: resolveFields</p>
	 * <p>Description: 解析字段表</p>
	 * @param begin
	 * @return
	 */
	private int resolveFields(int begin) {
		System.out.println("parseField: ");
		ByteUtils.printBytes(byteClass, begin, byteClass.length - begin);
		int fieldsCount = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		fields = new FieldInfo[fieldsCount];
		for (int i = 0; i < fieldsCount; ++i) {
			fields[i] = new FieldInfo();
			begin = fields[i].read(byteClass, begin, pool);
		}
		
		return begin;
	}
	
	/**
	 * <p>Title: resolveMethods</p>
	 * <p>Description: 解析方法表</p>
	 * @param begin
	 * @return
	 */
	private int resolveMethods(int begin) {
		System.out.println("parseMethod: ");
		ByteUtils.printBytes(byteClass, begin, byteClass.length - begin);
		int methodsCount = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		methods = new MethodInfo[methodsCount];
		for (int i = 0; i < methodsCount; ++i) {
			System.out.println("parse method " + i);
			methods[i] = new MethodInfo();
			begin = methods[i].read(byteClass, begin, pool);
			ByteUtils.printBytes(byteClass, begin, byteClass.length - begin);
		}
		
		return begin;
	}
	
	/**
	 * <p>Title: resolveAttrs</p>
	 * <p>Description: 解析属性表</p>
	 * @param begin
	 * @return
	 */
	private int resolveAttrs(int begin) {
		ByteUtils.printBytes(byteClass, begin, byteClass.length - begin);
		int attributesCount = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		attributes = new AbstractAttributeInfo[attributesCount];
		for (int i = 0; i < attributesCount; ++i) {
			byte[] byteNameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			int attributeNameIndex = ByteUtils.bytesToInt(byteNameIndex);
			begin += Constant.U2;
			ConstantType c = pool[attributeNameIndex];
			if (c == null) {
				throw new RuntimeException("class格式错误：attribute的attributeNameIndex指向了常量池中的第" + attributeNameIndex + "个常量，但该常量为null");
			}
			if (!(c instanceof Utf8Info)) {
				throw new RuntimeException("class格式错误：attribute的attributeNameIndex必须指向UtfiInfo，实际为常量池中的第" + attributeNameIndex + "个常量——" + c.getClass());
			}

			Utf8Info u = (Utf8Info)c;
			String attrType = new String(u.getBytes());
			attributes[i] = AttributeInfoFactory.build(attrType);
			attributes[i].setAttributeNameIndex(byteNameIndex);
			if (attributes[i] == null) {
				throw new RuntimeException("报错： attribute[i] == null ######################################################################################## i = " + i);
			}
			begin = attributes[i].read(byteClass, begin, pool);
		}
		
		return begin;
	}
	
	/**
	 * <p>Title: size</p>
	 * <p>Description: 获得整个对象树的大小</p>
	 * @return
	 */
	public int size() {
		int size = POOL_OFFSET + Constant.U2;//魔数、版本、常量池数
		if (pool != null) {
			for (int i = 1; i < pool.length; ++i) {
				size += pool[i].size();
				if (pool[i] instanceof LongInfo || pool[i] instanceof DoubleInfo) {
					++i;//Long和Double占两个位置
				}
			}
		}
		size += Constant.U2 + Constant.U2 + Constant.U2;//access、this_class、super_class

		size += Constant.U2;//interface length
		if (interfaces != null) {
			size += (interfaces.length * Constant.U2);
		}
		
		size += Constant.U2;//field length
		if (fields != null) {
			for (int i = 0; i < fields.length; ++i) {
				size += fields[i].size();
			}
		}
		size += Constant.U2;//method length
		if (methods != null) {
			for (int i = 0; i < methods.length; ++i) {
				size += methods[i].size();
			}
		}
		size += Constant.U2;//attribute length
		if (attributes != null) {
			for (int i = 0; i < attributes.length; ++i) {
				size += attributes[i].size();
			}
		}
		return size;
	}
	
	/**
	 * <p>Title: genBytes</p>
	 * <p>Description: 产生数组</p>
	 * @return
	 */
	public byte[] genBytes() {
		byte[] newBytes = null;
		int len = size();
		newBytes = new byte[len];
		ByteUtils.copy(MAGIC_NUMBER, newBytes, 0);
		ByteUtils.copy(minorVersion, newBytes, MAGIC_NUMBER.length);
		
		int begin = MAGIC_NUMBER.length + minorVersion.length;
		begin += ByteUtils.copy(majorVersion, newBytes, begin);
		
		if (pool != null) {
			byte[] tmp = ByteUtils.intToBytes(pool.length, Constant.U2);
			begin += ByteUtils.copy(tmp, newBytes, begin);
			for (int i = 1; i < pool.length; ++i) {
				begin = pool[i].write(newBytes, begin);
				if (pool[i] instanceof LongInfo || pool[i] instanceof DoubleInfo) {
					++i;
				}
			}
		} else {
			newBytes[begin] = newBytes[begin + 1] = 0;
			begin += 2;
		}

		begin += ByteUtils.copy(access, newBytes, begin);
		begin += ByteUtils.copy(thisClass, newBytes, begin);
		begin += ByteUtils.copy(superClass, newBytes, begin);
	
		if (interfaces != null) {
			byte[] tmp = ByteUtils.intToBytes(interfaces.length, Constant.U2);
			begin += ByteUtils.copy(tmp, newBytes, begin);
			for (int i = 0; i < interfaces.length; ++i) {
				begin += ByteUtils.copy(interfaces[i], newBytes, begin);
			}
		} else {
			newBytes[begin] = newBytes[begin + 1] = 0;
			begin += 2;
		}
	
		if (fields != null) {
			byte[] tmp = ByteUtils.intToBytes(fields.length, Constant.U2);
			begin += ByteUtils.copy(tmp, newBytes, begin);
			for (int i = 0; i < fields.length; ++i) {
				begin = fields[i].write(newBytes, begin);
			}
		} else {
			newBytes[begin] = newBytes[begin + 1] = 0;
			begin += 2;
		}
		
		if (methods != null) {
			byte[] tmp = ByteUtils.intToBytes(methods.length, Constant.U2);
			begin += ByteUtils.copy(tmp, newBytes, begin);
			
			for (int i = 0; i < methods.length; ++i) {
				begin = methods[i].write(newBytes, begin);
			}
		} else {
			newBytes[begin] = newBytes[begin + 1] = 0;
			begin += 2;
		}
		
		if (attributes != null) {
			byte[] tmp = ByteUtils.intToBytes(attributes.length, Constant.U2);
			begin += ByteUtils.copy(tmp, newBytes, begin);
			for (int i = 0; i < attributes.length; ++i) {
				begin = attributes[i].write(newBytes, begin);
			}
		} else {
			newBytes[begin] = newBytes[begin + 1] = 0;
			begin += 2;
		}
		
		return newBytes;
	}
	
	//以下是getter和setter

	public ConstantType[] getPool() {
		return pool;
	}
	
	public byte[] getAccess() {
		return this.access;
	}

	public byte[] getThisClass() {
		return thisClass;
	}

	public byte[] getSuperClass() {
		return superClass;
	}

	public byte[][] getInterfaces() {
		return interfaces;
	}
	
	public int interfaceSize() {
		return interfaces.length;
	}

	public byte[] getByteClass() {
		return byteClass;
	}

	public void setByteClass(byte[] byteClass) {
		this.byteClass = byteClass;
	}

	public byte[] getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(byte[] minorVersion) {
		this.minorVersion = minorVersion;
	}

	public byte[] getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(byte[] majorVersion) {
		this.majorVersion = majorVersion;
	}

	public FieldInfo[] getFields() {
		return fields;
	}

	public void setFields(FieldInfo[] fields) {
		this.fields = fields;
	}

	public MethodInfo[] getMethods() {
		return methods;
	}

	public void setMethods(MethodInfo[] methods) {
		this.methods = methods;
	}

	public AbstractAttributeInfo[] getAttributes() {
		return attributes;
	}

	public void setAttributes(AbstractAttributeInfo[] attributes) {
		this.attributes = attributes;
	}

	public void setPool(ConstantType[] pool) {
		this.pool = pool;
	}

	public void setAccess(byte[] access) {
		this.access = access;
	}

	public void setThisClass(byte[] thisClass) {
		this.thisClass = thisClass;
	}

	public void setSuperClass(byte[] superClass) {
		this.superClass = superClass;
	}

	public void setInterfaces(byte[][] interfaces) {
		this.interfaces = interfaces;
	}
}
