package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class ConstantValueAttr extends AbstractAttributeInfo {
	private byte[] constantvalueIndex;
	
	public byte[] getConstantvalueIndex() {
		return constantvalueIndex;
	}
	
	public void setConstantvalueIndex(byte[] constantvalueIndex) {
		if (constantvalueIndex == null || constantvalueIndex.length != Constant.U2) {
			throw new RuntimeException("ConstantValueAttr的constantvalueIndex不能为null且长度必须为2");
		}
		this.constantvalueIndex = constantvalueIndex;
	}
	
	@Override
	public int getAttributeLength() {
		return Constant.U2;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		begin += ByteUtils.copy(constantvalueIndex, byteClass, begin);
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		constantvalueIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
