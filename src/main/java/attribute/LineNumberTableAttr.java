package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class LineNumberTableAttr extends AbstractAttributeInfo {
	private LineNumberInfo[] lineNumberTable;
	
	private int resolveLineNumber(byte[] byteClass, int begin) {
		int lineNumberTableLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		lineNumberTable = new LineNumberInfo[lineNumberTableLength];
		for (int i = 0; i < lineNumberTable.length; ++i) {
			lineNumberTable[i] = new LineNumberInfo();
			begin = lineNumberTable[i].read(byteClass, begin);
		}
		
		return begin;
	}
	
	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (lineNumberTable != null) {
			for (int i = 0; i < lineNumberTable.length; ++i) {
				len += lineNumberTable[i].size();
			}
		}
		return len;
	}

	public LineNumberInfo[] getLineNumberTable() {
		return lineNumberTable;
	}

	public void setLineNumberTable(LineNumberInfo[] lineNumberTable) {
		this.lineNumberTable = lineNumberTable;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (lineNumberTable == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(lineNumberTable.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < lineNumberTable.length; ++i) {
				begin = lineNumberTable[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		begin = resolveLineNumber(byteClass, begin);
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
