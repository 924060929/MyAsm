package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class MethodTypeInfo implements ConstantType {
	private byte[] descriptorIndex;
	
	public byte[] getDescriptorIndex() {
		return descriptorIndex;
	}
	
	public void setDescriptorIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("MethodTypeInfo的descriptorIndex不能为空且长度必须为2字节");
		}
		this.descriptorIndex = index;
	}

	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_MethodType_info;
	}

	@Override
	public int size() {//tag、descriptorIndex
		return Constant.U1 + Constant.U2;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(descriptorIndex, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		descriptorIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
