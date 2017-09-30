package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class StackMapTableAttr extends AbstractAttributeInfo {
	private StackMapFrame[] entries;
	
	public StackMapFrame[] getEntries() {
		return entries;
	}
	
	public void setEntries(StackMapFrame[] entries) {
		this.entries = entries;
	}
	
	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (entries != null) {
			for (int i = 0; i < entries.length; ++i) {
				len += entries[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (entries == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(entries.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < entries.length; ++i) {
				begin = entries[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		int numberOfEntries = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		entries = new StackMapFrame[numberOfEntries];
		for (int i = 0; i < entries.length; ++i) {
			entries[i] = new StackMapFrame();
			begin = entries[i].read(byteClass, begin);
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
