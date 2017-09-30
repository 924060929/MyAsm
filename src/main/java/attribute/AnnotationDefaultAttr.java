package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class AnnotationDefaultAttr extends AbstractAttributeInfo {
	private ElementValue defaultValue;
	
	public ElementValue getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(ElementValue defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public int getAttributeLength() {
		if (defaultValue == null) {
			return 0;
		}
		return defaultValue.size();
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		begin = defaultValue.write(byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		defaultValue = new ElementValue();
		return defaultValue.read(byteClass, begin);
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
