package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class NameAndTypeInfo implements ConstantType {
	private byte[] index;
	private byte[] index2;
	
	public byte[] getIndex() {
		return index;
	}
	
	public void setIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("NameAndTypeInfo的index不能为空且长度必须为2字节");
		}
		this.index = index;
	}
	
	public byte[] getIndex2() {
		return index2;
	}

	public void setIndex2(byte[] index2) {
		if (index2 == null || index2.length != Constant.U2) {
			throw new RuntimeException("NameAndTypeInfo的index2不能为空且长度必须为2字节");
		}
		this.index2 = index2;
	}

	@Override
	public int getTag() {//tag、index、index2
		return ConstantPoolTag.CONSTANT_NameAndType_info;
	}

	@Override
	public int size() {
		return Constant.U1 + Constant.U2 + Constant.U2;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(index, byteClass, begin);
		begin += ByteUtils.copy(index2, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		index = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		index2 = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
