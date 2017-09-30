package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class InnerClassesAttr extends AbstractAttributeInfo {
	private InnerClassesInfo[] innerClasses;
	
	private int resolveInnerClass(byte[] byteClass, int begin) {
		int numberOfClasses = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		innerClasses = new InnerClassesInfo[numberOfClasses];
		for (int i = 0; i < numberOfClasses; ++i) {
			innerClasses[i] = new InnerClassesInfo();
			begin = innerClasses[i].read(byteClass, begin);
		}
		
		return begin;
	}
	
	public InnerClassesInfo[] getInnerClasses() {
		return innerClasses;
	}
	
	public void setInnerClasses(InnerClassesInfo[] innerClasses) {
		this.innerClasses = innerClasses;
	}

	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (innerClasses != null) {
			for (int i = 0; i < innerClasses.length; ++i) {
				len += innerClasses[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (innerClasses == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(innerClasses.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < innerClasses.length; ++i) {
				begin = innerClasses[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		begin = resolveInnerClass(byteClass, begin);
		return begin;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
