package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class InvokeDynamicInfo implements ConstantType {
	private byte[] bootstrapMethodAttrIndex;
	private byte[] nameAndTypeIndex;
	
	public byte[] getBootstrapMethodAttrIndex() {
		return bootstrapMethodAttrIndex;
	}
	
	public void setBootstrapMethodAttrIndex(byte[] methodIndex) {
		if (methodIndex == null || methodIndex.length != Constant.U2) {
			throw new RuntimeException("InvokeDynamicInfo的bootstrapMethodAttrIndex不能为空且长度必须为2字节");
		}
		this.bootstrapMethodAttrIndex = methodIndex;
	}

	public byte[] getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}
	
	public void setNameAndTypeIndex(byte[] nameAndTypeIndex) {
		if (nameAndTypeIndex == null || nameAndTypeIndex.length != Constant.U2) {
			throw new RuntimeException("InvokeDynamicInfo的nameAndTypeIndex不能为空且长度必须为2字节");
		}
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_InvokeDynamic_info;
	}

	@Override
	public int size() {//tag、bootstrapMethodAttrIndex、nameAndTypeIndex
		return Constant.U1 + Constant.U2 + Constant.U2;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(bootstrapMethodAttrIndex, byteClass, begin);
		begin += ByteUtils.copy(nameAndTypeIndex, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		bootstrapMethodAttrIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		nameAndTypeIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
