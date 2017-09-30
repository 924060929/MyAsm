package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class EnclosingMethodAttr extends AbstractAttributeInfo {
	private byte[] classIndex;
	private byte[] methodIndex;
	
	public byte[] getClassIndex() {
		return classIndex;
	}
	
	public void setClassIndex(byte[] classIndex) {
		if (classIndex == null || classIndex.length != Constant.U2) {
			throw new RuntimeException("EnclosingMethodAttr的classIndex不能为null且长度必须为2字节");
		}
		this.classIndex = classIndex;
	}
	
	public byte[] getMethodIndex() {
		return methodIndex;
	}
	
	public void setMethodIndex(byte[] methodIndex) {
		if (methodIndex == null || methodIndex.length != Constant.U2) {
			throw new RuntimeException("EnclosingMethodAttr的methodIndex不能为null且长度必须为2字节");
		}
		this.methodIndex = methodIndex;
	}
	
	@Override
	public int getAttributeLength() {
		return Constant.U4;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		begin += ByteUtils.copy(classIndex, byteClass, begin);
		begin += ByteUtils.copy(methodIndex, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		classIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		methodIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
