package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class LocalVariableTypeTableAttr extends AbstractAttributeInfo {
	private LocalVariableTypeTable[] tables;
	
	public LocalVariableTypeTable[] getTables() {
		return tables;
	}
	
	public void setTables(LocalVariableTypeTable[] tables) {
		this.tables = tables;
	}
	
	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (tables != null) {
			for (int i = 0; i < tables.length; ++i) {
				len += tables[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (tables == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(tables.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < tables.length; ++i) {
				begin = tables[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		int localVariableTypeTableLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		tables = new LocalVariableTypeTable[localVariableTypeTableLength];
		for (int i = 0; i < tables.length; ++i) {
			tables[i] = new LocalVariableTypeTable();
			begin = tables[i].read(byteClass, begin);
		}
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
