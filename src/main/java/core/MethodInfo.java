package core;

import constant.ClassInfo;
import constant.ConstantType;
import constant.Utf8Info;
import attribute.AbstractAttributeInfo;
import attribute.AttributeInfoFactory;
import utils.ByteUtils;

/**
 * <p>Title: MethodInfo</p>
 * <p>Description: 这是方法表的类，是对象树中的一员，被ClassImage包装</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午3:20:25
 */
public class MethodInfo {
	private byte[] accessFlags;
	private byte[] nameIndex;
	private byte[] descriptorIndex;
	private AbstractAttributeInfo[] attributes;
	
	private int resolveAttrs(byte[] byteClass, int begin, ConstantType[] pool) {
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
	
	public int size() {
		//accessFlags、nameIndex、descriptorIndex、attributesCount
		int size = Constant.U8;
		if (attributes != null) {
			for (int i = 0; i < attributes.length; ++i) {
				size += attributes[i].size();
			}
		}
		return size;
	}

	public byte[] getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(byte[] accessFlags) {
		if (accessFlags == null || accessFlags.length != Constant.U2) {
			throw new RuntimeException("MethodInfo的accessFlags不能为null且长度必须为2字节");
		}
		this.accessFlags = accessFlags;
	}

	public byte[] getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(byte[] nameIndex) {
		if (nameIndex == null || nameIndex.length != Constant.U2) {
			throw new RuntimeException("MethodInfo的nameIndex不能为null且长度必须为2字节");
		}
		this.nameIndex = nameIndex;
	}

	public byte[] getDescriptorIndex() {
		return descriptorIndex;
	}

	public void setDescriptorIndex(byte[] descriptorIndex) {
		if (descriptorIndex == null || descriptorIndex.length != Constant.U2) {
			throw new RuntimeException("MethodInfo的descriptorIndex不能为null且长度必须为2字节");
		}
		this.descriptorIndex = descriptorIndex;
	}

	public AbstractAttributeInfo[] getAttributes() {
		return attributes;
	}

	public void setAttributes(AbstractAttributeInfo[] attributes) {
		this.attributes = attributes;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(accessFlags, byteClass, begin);
		begin += ByteUtils.copy(nameIndex, byteClass, begin);
		begin += ByteUtils.copy(descriptorIndex, byteClass, begin);
		if (attributes == null) {
			byteClass[begin] = byteClass[begin + 1] = 0;
			begin += 2;
		} else {
			byte[] attributeLength = ByteUtils.intToBytes(attributes.length, Constant.U2);
			begin += ByteUtils.copy(attributeLength, byteClass, begin);
			for (int i = 0; i < attributes.length; ++i) {
				begin = attributes[i].write(byteClass, begin);
			}
		}
		
		return begin;
	}
	
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		accessFlags = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		nameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		int intNameIndex = ByteUtils.bytesToInt(nameIndex);
		if (pool[intNameIndex] instanceof Utf8Info) {
			System.out.println("parse method: " + new String(((Utf8Info)pool[intNameIndex]).getBytes()));
//			ClassImage.console.println("parse method: " + new String(((Utf8Info)pool[intNameIndex]).getBytes()));
		} else if (pool[intNameIndex] instanceof ClassInfo) {
			ClassInfo c = (ClassInfo)pool[intNameIndex];
			Utf8Info u = (Utf8Info)pool[ByteUtils.bytesToInt(c.getIndex())];
			System.out.println("parse method: " + new String(u.getBytes()));
//			ClassImage.console.println("parse method: " + new String(u.getBytes()));
		} else {
			throw new RuntimeException("不支持类型：" + pool[intNameIndex].getClass());
		}
		descriptorIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		begin = resolveAttrs(byteClass, begin, pool);
		
		return begin;
	}
}
