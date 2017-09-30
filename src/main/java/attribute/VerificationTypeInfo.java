package attribute;

import core.Constant;
import utils.ByteUtils;

public class VerificationTypeInfo {
	private byte tag;
	private byte[] cpoolIndex;
	private byte[] offset;
	
	public int size() {
		int size = Constant.U1;
		if (tag == 7) {
			size += Constant.U2;
		} else if (tag == 8) {
			size += Constant.U2;
		}
		return size;
	}
	
	public byte getTag() {
		return tag;
	}
	
	public void setTag(byte tag) {
		this.tag = tag;
	}
	
	public byte[] getCpoolIndex() {
		return cpoolIndex;
	}
	
	public void setCpoolIndex(byte[] cpoolIndex) {
		if (cpoolIndex == null || cpoolIndex.length != Constant.U2) {
			throw new RuntimeException("VerificationTypeInfo的cpoolIndex不能为null且长度必须为2字节");
		}
		this.cpoolIndex = cpoolIndex;
	}
	public byte[] getOffset() {
		return offset;
	}
	
	public void setOffset(byte[] offset) {
		if (offset == null || offset.length != Constant.U2) {
			throw new RuntimeException("VerificationTypeInfo的offset不能为null且长度必须为2字节");
		}
		this.offset = offset;
	}
	
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = tag;
		if (tag == 7) {
			begin += ByteUtils.copy(cpoolIndex, byteClass, begin);
		} else if (tag == 8) {
			begin += ByteUtils.copy(offset, byteClass, begin);
		}
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		tag = byteClass[begin++];
		if (tag == 7) {
			cpoolIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		} else if (tag == 8) {
			offset = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		}
		return begin;
	}
}
