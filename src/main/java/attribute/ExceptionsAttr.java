package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class ExceptionsAttr extends AbstractAttributeInfo  {
	private byte[][] exceptionIndexTable;
	
	public int resolveExceptionIndex(byte[] byteClass, int begin) {
		int numberOfExceptions = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		exceptionIndexTable = new byte[numberOfExceptions][];
		for (int i = 0; i < numberOfExceptions; ++i) {
			exceptionIndexTable[i] = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		}
		
		return begin;
	}

	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (exceptionIndexTable != null) {
			len += (Constant.U2 * exceptionIndexTable.length);
		}
		return len;
	}

	public byte[][] getExceptionIndexTable() {
		return exceptionIndexTable;
	}

	public void setExceptionIndexTable(byte[][] exceptionIndexTable) {
		if (exceptionIndexTable != null) {
			for (byte[] index : exceptionIndexTable) {
				if (index == null || index.length != Constant.U2) {
					throw new RuntimeException("ExceptionAttr的exceptionIndexTable的数组元素不能为null且长度必须为2字节");
				}
			}
		}
		this.exceptionIndexTable = exceptionIndexTable;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (exceptionIndexTable == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(exceptionIndexTable.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < exceptionIndexTable.length; ++i) {
				begin += ByteUtils.copy(exceptionIndexTable[i], byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		begin = resolveExceptionIndex(byteClass, begin);
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
