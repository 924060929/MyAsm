package attribute;

import core.Constant;
import utils.ByteUtils;

public class LocalVariableInfo {
	private byte[] startPc;
	private byte[] length;
	private byte[] nameIndex;
	private byte[] descriptorIndex;
	private byte[] index;
	
	public int size() {
		return Constant.U2 + Constant.U2 + Constant.U2 + Constant.U2 + Constant.U2;
	}
	
	public byte[] getStartPc() {
		return startPc;
	}
	
	public void setStartPc(byte[] startPc) {
		if (startPc == null || startPc.length != Constant.U2) {
			throw new RuntimeException("LocalVariableInfo的startPc不能为null且长度必须为2字节");
		}
		this.startPc = startPc;
	}
	
	public byte[] getLength() {
		return length;
	}
	
	public void setLength(byte[] length) {
		if (length == null || length.length != Constant.U2) {
			throw new RuntimeException("LocalVariableInfo的length不能为null且长度必须为2字节");
		}
		this.length = length;
	}
	
	public byte[] getNameIndex() {
		return nameIndex;
	}
	
	public void setNameIndex(byte[] nameIndex) {
		if (nameIndex == null || nameIndex.length != Constant.U2) {
			throw new RuntimeException("LocalVariableInfo的nameIndex不能为null且长度必须为2字节");
		}
		this.nameIndex = nameIndex;
	}
	
	public byte[] getDescriptorIndex() {
		return descriptorIndex;
	}
	
	public void setDescriptorIndex(byte[] descriptorIndex) {
		if (descriptorIndex == null || descriptorIndex.length != Constant.U2) {
			throw new RuntimeException("LocalVariableInfo的descriptorIndex不能为null且长度必须为2字节");
		}
		this.descriptorIndex = descriptorIndex;
	}
	
	public byte[] getIndex() {
		return index;
	}
	
	public void setIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("LocalVariableInfo的index不能为null且长度必须为2字节");
		}
		this.index = index;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(startPc, byteClass, begin);
		begin += ByteUtils.copy(length, byteClass, begin);
		begin += ByteUtils.copy(nameIndex, byteClass, begin);
		begin += ByteUtils.copy(descriptorIndex, byteClass, begin);
		begin += ByteUtils.copy(index, byteClass, begin);
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		startPc = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		length = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		nameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		descriptorIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		index = ByteUtils.subBytes(byteClass, begin, Constant.U2);
	
		return begin + Constant.U2;
	}
}
