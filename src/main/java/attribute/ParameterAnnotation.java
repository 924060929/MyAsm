package attribute;

import core.Constant;
import utils.ByteUtils;

public class ParameterAnnotation {
	private Annotation[] annotations;
	
	public int size() {
		int size = Constant.U2;//numAnnotations
		if (annotations != null) {
			for (int i = 0; i < annotations.length; ++i) {
				size += annotations[i].size();
			}
		}
		
		return size;
	}
	
	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
	
	public int write(byte[] byteClass, int begin) {
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
	
	public int read(byte[] byteClass, int begin) {
		int numAnnotations = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		annotations = new Annotation[numAnnotations];
		for (int i = 0; i < annotations.length; ++i) {
			annotations[i] = new Annotation();
			begin = annotations[i].read(byteClass, begin);
		}
		return begin;
	}
}
