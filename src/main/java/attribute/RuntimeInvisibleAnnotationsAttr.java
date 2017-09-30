package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class RuntimeInvisibleAnnotationsAttr extends AbstractAttributeInfo {
	private Annotation[] annotations;
	
	public Annotation[] getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
	
	@Override
	public int getAttributeLength() {
		int len = Constant.U2;
		if (annotations != null) {
			for (int i = 0; i < annotations.length; ++i) {
				len += annotations[i].size();
			}
		}
		return len;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		if (annotations == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(annotations.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < annotations.length; ++i) {
				begin = annotations[i].write(byteClass, begin);
			}
		}
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		int numAnnotations = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		annotations = new Annotation[numAnnotations];
		for (int i = 0; i < annotations.length; ++i) {
			annotations[i] = new Annotation();
			begin = annotations[i].read(byteClass, begin);
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
