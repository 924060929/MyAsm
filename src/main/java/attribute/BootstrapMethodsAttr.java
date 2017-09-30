package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class BootstrapMethodsAttr extends AbstractAttributeInfo {
	private BootstrapMethod[] bootstrapMethods;
	
	public int resolveMethods(byte[] byteClass, int begin) {
		int numBootstrapMethods = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
		begin += Constant.U2;
		for (int i = 0; i < numBootstrapMethods; ++i) {
			bootstrapMethods[i] = new BootstrapMethod();
			begin = bootstrapMethods[i].read(byteClass, begin);
		}
		return begin;
	}
	
	public BootstrapMethod[] getBootstrapMethods() {
		return bootstrapMethods;
	}
	
	public void setBootstrapMethods(BootstrapMethod[] bootstrapMethods) {
		this.bootstrapMethods = bootstrapMethods;
	}

	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (bootstrapMethods != null) {
			for (int i = 0; i < bootstrapMethods.length; ++i) {
				len += bootstrapMethods[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (bootstrapMethods == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(bootstrapMethods.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < bootstrapMethods.length; ++i) {
				begin = bootstrapMethods[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		begin = resolveMethods(byteClass, begin);
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
	
}
