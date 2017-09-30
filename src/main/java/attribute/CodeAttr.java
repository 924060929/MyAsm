package attribute;

import constant.ConstantType;
import constant.Utf8Info;
import core.Constant;
import utils.ByteUtils;

public class CodeAttr extends AbstractAttributeInfo {
	private byte[] maxStack;
	private byte[] maxLocals;
	private byte[] codes;
	private ExceptionInfo[] exceptionTable;
	private AbstractAttributeInfo[] attributes;
	
	private int resolveCode(byte[] byteClass, int begin) {
		int codeLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U4);
		codes = new byte[codeLength];
		begin += Constant.U4;
		for (int i = 0; i < codes.length; ++i) {
			codes[i] = byteClass[begin];
			begin += Constant.U1;
		}
		System.out.println("\n-----------------------------code");
		ByteUtils.printBytes(byteClass, begin, byteClass.length - begin);
		
		return begin;
	}
	
	private int resolveException(byte[] byteClass, int begin) {
		int exceptionTableLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		exceptionTable = new ExceptionInfo[exceptionTableLength];
		begin += Constant.U2;
		for (int i = 0; i < exceptionTableLength; ++i) {
			exceptionTable[i] = new ExceptionInfo();
			begin = exceptionTable[i].read(byteClass, begin);
		}
		
		return begin;
	}
	
	private int resolveAttribute(byte[] byteClass, int begin, ConstantType[] pool) {
		int attributesCount = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		attributes = new AbstractAttributeInfo[attributesCount];
		begin += Constant.U2;
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
	

	public byte[] getMaxStack() {
		return maxStack;
	}
	
	public void setMaxStack(byte[] maxStack) {
		if (maxStack == null || maxStack.length != Constant.U2) {
			throw new RuntimeException("CodeAttr的maxStack不能为null且长度必须为2字节");
		}
		this.maxStack = maxStack;
	}
	
	public byte[] getMaxLocals() {
		return maxLocals;
	}
	
	public void setMaxLocals(byte[] maxLocals) {
		if (maxLocals == null || maxLocals.length != Constant.U2) {
			throw new RuntimeException("CodeAttr的maxLocals不能为null且长度必须为2字节");
		}
		this.maxLocals = maxLocals;
	}
	
	public byte[] getCodes() {
		return codes;
	}
	
	public void setCodes(byte[] codes) {
		this.codes = codes;
	}
	
	public ExceptionInfo[] getExceptionTable() {
		return exceptionTable;
	}
	
	public void setExceptionTable(ExceptionInfo[] exceptionTable) {
		this.exceptionTable = exceptionTable;
	}
	
	public AbstractAttributeInfo[] getAttributes() {
		return attributes;
	}
	
	public void setAttributes(AbstractAttributeInfo[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public int getAttributeLength() {
		int len = Constant.U2 + Constant.U2;//最大栈帧数+最大局部变量数
		len += Constant.U4;//Code长度
		if (codes != null) {
			len += codes.length;
		}
		len += Constant.U2;//异常表数
		if (exceptionTable != null) {
			for (ExceptionInfo e : exceptionTable) {
				len += e.size();
			}
		}
		len += Constant.U2;//属性表数
		if (attributes != null) {
			for (AbstractAttributeInfo a : attributes) {
				len += a.size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		begin += ByteUtils.copy(maxStack, byteClass, begin);
		begin += ByteUtils.copy(maxLocals, byteClass, begin);
		if (codes == null) {
			byteClass[begin] = byteClass[begin+1] = byteClass[begin+2] = byteClass[begin+3] = 0;
			begin += Constant.U4;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(codes.length, Constant.U4), byteClass, begin);
			for (int i = 0; i < codes.length; ++i) {
				byteClass[begin++] = codes[i];
			}
		}
		if (exceptionTable == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(exceptionTable.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < exceptionTable.length; ++i) {
				begin = exceptionTable[i].write(byteClass, begin);
			}
		}
		if (attributes == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(attributes.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < attributes.length; ++i) {
				begin = attributes[i].write(byteClass, begin);
			}
		}
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		maxStack = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		maxLocals = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		begin = resolveCode(byteClass, begin);
		begin = resolveException(byteClass, begin);
		begin = resolveAttribute(byteClass, begin, pool);
		return begin;
	}

	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
	
}
