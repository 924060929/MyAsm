package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class LocalVariableTableAttr extends AbstractAttributeInfo {
	private LocalVariableInfo[] localVariableTable;
	
	private int resolveLocalVariable(byte[] byteClass, int begin) {
		int localVariableTableLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		localVariableTable = new LocalVariableInfo[localVariableTableLength];
		for (int i = 0; i < localVariableTable.length; ++i) {
			localVariableTable[i] = new LocalVariableInfo();
			begin = localVariableTable[i].read(byteClass, begin);
		}
		
		return begin;
	}
	
	public LocalVariableInfo[] getLocalVariableTable() {
		return localVariableTable;
	}
	
	public void setLocalVariableTable(LocalVariableInfo[] localVariableTable) {
		this.localVariableTable = localVariableTable;
	}

	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (localVariableTable != null) {
			for (int i = 0; i < localVariableTable.length; ++i) {
				len += localVariableTable[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (localVariableTable == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(localVariableTable.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < localVariableTable.length; ++i) {
				begin = localVariableTable[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin = resolveLocalVariable(byteClass, begin + Constant.U4);
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
