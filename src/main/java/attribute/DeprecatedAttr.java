package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class DeprecatedAttr extends AbstractAttributeInfo {
	
	@Override
	public int getAttributeLength() {
		return 0;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		return begin + Constant.U4;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
