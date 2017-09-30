package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class RuntimeInvisibleParameterAnnotationsAttr extends AbstractAttributeInfo {
	private ParameterAnnotation[] parameters;
	
	public ParameterAnnotation[] getParameters() {
		return parameters;
	}
	
	public void setParameters(ParameterAnnotation[] parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (parameters != null) {
			for (int i = 0; i < parameters.length; ++i) {
				len += parameters[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (parameters == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(parameters.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < parameters.length; ++i) {
				begin = parameters[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		int numParameters = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		parameters = new ParameterAnnotation[numParameters];
		for (int i = 0; i < parameters.length; ++i) {
			parameters[i] = new ParameterAnnotation();
			begin = parameters[i].read(byteClass, begin);
		}
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
