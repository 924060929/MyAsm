package attribute;

import core.Constant;
import utils.ByteUtils;

public class BootstrapMethod {
	private byte[] bootstrapMethodRef;
	private byte[][] bootstrapArguments;
	
	private int resolveArg(byte[] byteClass, int begin) {
		int numBootstrapArguments = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		bootstrapArguments = new byte[numBootstrapArguments][];
		for (int i = 0; i < numBootstrapArguments; ++i) {
			bootstrapArguments[i] = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		}
		return begin;
	}
	
	public int size() {
		int size = Constant.U4;//bootstrapMethodRef、numBootstrapArguments
		size += bootstrapArguments.length * Constant.U2;
		return size;
	}
	
	public byte[] getBootstrapMethodRef() {
		return bootstrapMethodRef;
	}
	
	public void setBootstrapMethodRef(byte[] bootstrapMethodRef) {
		if (bootstrapMethodRef == null || bootstrapMethodRef.length != Constant.U2) {
			throw new RuntimeException("BootstrapMethod的bootstrapMethodRef不能为null且长度必须为2字节");
		}
		this.bootstrapMethodRef = bootstrapMethodRef;
	}

	public byte[][] getBootstrapArguments() {
		return bootstrapArguments;
	}

	public void setBootstrapArguments(byte[][] bootstrapArguments) {
		if (bootstrapArguments != null) {
			for (int i = 0; i < bootstrapArguments.length; ++i) {
				if (bootstrapArguments[i] == null || bootstrapArguments[i].length != Constant.U2) {
					throw new RuntimeException("BootstrapMethod的bootstrapArguments的数组元素不能为null且长度必须为2字节");
				}
			}
		}
		this.bootstrapArguments = bootstrapArguments;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(bootstrapMethodRef, byteClass, begin);
		if (bootstrapArguments == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(bootstrapArguments.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < bootstrapArguments.length; ++i) {
				begin += ByteUtils.copy(bootstrapArguments[i], byteClass, begin);
			}
		}
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		bootstrapMethodRef = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		begin = resolveArg(byteClass, begin);
		return begin;
	}
}
