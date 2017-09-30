package attribute;

import core.Constant;
import utils.ByteUtils;

public class InnerClassesInfo {
	private byte[] innerClassInfoIndex;
	private byte[] outerClassInfoIndex;
	private byte[] innerNameIndex;
	private byte[] innerClassAccessFlags;
	
	public int size() {
		return Constant.U8;
	}
	
	public byte[] getInnerClassInfoIndex() {
		return innerClassInfoIndex;
	}
	public void setInnerClassInfoIndex(byte[] innerClassInfoIndex) {
		if (innerClassInfoIndex == null || innerClassInfoIndex.length != Constant.U2) {
			throw new RuntimeException("InnerClassesInfo的innerClassInfoIndex不能为null且长度必须为2字节");
		}
		this.innerClassInfoIndex = innerClassInfoIndex;
	}
	public byte[] getOuterClassInfoIndex() {
		return outerClassInfoIndex;
	}
	public void setOuterClassInfoIndex(byte[] outerClassInfoIndex) {
		if (outerClassInfoIndex == null || outerClassInfoIndex.length != Constant.U2) {
			throw new RuntimeException("InnerClassesInfo的outerClassInfoIndex不能为null且长度必须为2字节");
		}
		this.outerClassInfoIndex = outerClassInfoIndex;
	}
	public byte[] getInnerNameIndex() {
		return innerNameIndex;
	}
	public void setInnerNameIndex(byte[] innerNameIndex) {
		if (innerNameIndex == null || innerNameIndex.length != Constant.U2) {
			throw new RuntimeException("InnerClassesInfo的innerNameIndex不能为null且长度必须为2字节");
		}
		this.innerNameIndex = innerNameIndex;
	}
	public byte[] getInnerClassAccessFlags() {
		return innerClassAccessFlags;
	}
	public void setInnerClassAccessFlags(byte[] innerClassAccessFlags) {
		if (innerClassAccessFlags == null || innerClassAccessFlags.length != Constant.U2) {
			throw new RuntimeException("InnerClassesInfo的innerClassAccessFlags不能为null且长度必须为2字节");
		}
		this.innerClassAccessFlags = innerClassAccessFlags;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(innerClassInfoIndex, byteClass, begin);
		begin += ByteUtils.copy(outerClassInfoIndex, byteClass, begin);
		begin += ByteUtils.copy(innerNameIndex, byteClass, begin);
		begin += ByteUtils.copy(innerClassAccessFlags, byteClass, begin);
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		innerClassInfoIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		outerClassInfoIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		innerNameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		innerClassAccessFlags = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
