package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class MethodHandleInfo implements ConstantType {
	private byte referenceKind;
	private byte[] referenceIndex;
	
	public byte getReferenceKind() {
		return referenceKind;
	}
	
	public void setReferenceKind(byte kind) {
		this.referenceKind = kind;
	}

	public byte[] getReferenceIndex() {
		return referenceIndex;
	}
	
	public void setReferenceIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("MethodHandleInfo的referenceIndex不能为空且长度必须为2");
		}
		this.referenceIndex = index;
	}

	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_MethodHandle_info;
	}

	@Override
	public int size() {//tag、referenceKind、referenceIndex
		return Constant.U1 + Constant.U1 + Constant.U2;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		byteClass[begin++] = referenceKind;
		begin += ByteUtils.copy(referenceIndex, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		referenceKind = byteClass[begin++];
		referenceIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
