package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class SourceDebugExtensionAttr extends AbstractAttributeInfo {
	private byte[] debugExtension;
	
	public byte[] getDebugExtension() {
		return debugExtension;
	}
	
	public void setDebugExtension(byte[] debugExtension) {
		this.debugExtension = debugExtension;
	}
	
	@Override
	public int getAttributeLength() {
		if (debugExtension == null) {
			return 0;
		}
		return debugExtension.length;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (debugExtension == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(debugExtension.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < debugExtension.length; ++i) {
				byteClass[begin++] = debugExtension[i];
			}
		}
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		int attributeLength = ByteUtils.bytesToInt(byteClass, begin, Constant.U4);
		begin += Constant.U4;
		debugExtension = new byte[attributeLength];
		for (int i = 0; i < debugExtension.length; ++i) {
			debugExtension[i] = byteClass[begin++];
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
