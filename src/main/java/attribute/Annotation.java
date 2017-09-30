package attribute;

import core.Constant;
import utils.ByteUtils;

public class Annotation {
	private byte[] typeIndex;
	private ElementValuePairs[] pairs;
	
	public int size() {
		int size = Constant.U4;//typeIndex、numElementValuePairs
		for (int i = 0; i < pairs.length; ++i) {
			size += pairs[i].size();
		}
		return size;
	}
	
	public static class ElementValuePairs {
		private byte[] elementNameIndex;
		private ElementValue value;
		
		public int size() {
			return Constant.U2 + value.size();
		}
		
		public byte[] getElementNameIndex() {
			return elementNameIndex;
		}
		public void setElementNameIndex(byte[] elementNameIndex) {
			if (elementNameIndex == null || elementNameIndex.length != Constant.U2) {
				throw new RuntimeException("ElementValuePairs的elementNameIndex不能为null且长度必须为2字节");
			}
			this.elementNameIndex = elementNameIndex;
		}
		public ElementValue getValue() {
			return value;
		}
		public void setValue(ElementValue value) {
			this.value = value;
		}
		public int write(byte[] byteClass, int begin) {
			begin += ByteUtils.copy(elementNameIndex, byteClass, begin);
			begin = value.write(byteClass, begin);
			return begin;
		}
		public int read(byte[] byteClass, int begin) {
			elementNameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			value = new ElementValue();
			begin = value.read(byteClass, begin);
			return begin;
		}
	}

	public byte[] getTypeIndex() {
		return typeIndex;
	}

	public void setTypeIndex(byte[] typeIndex) {
		if (typeIndex == null || typeIndex.length != Constant.U2) {
			throw new RuntimeException("Annotation的typeIndex不能为null且长度必须为2字节");
		}
		this.typeIndex = typeIndex;
	}

	public ElementValuePairs[] getPairs() {
		return pairs;
	}

	public void setPairs(ElementValuePairs[] pairs) {
		this.pairs = pairs;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(typeIndex, byteClass, begin);
		if (pairs == null) {
			byteClass[begin] = byteClass[begin+1] = 0;
			begin += Constant.U2;
		} else {
			begin += ByteUtils.copy(ByteUtils.intToBytes(pairs.length, Constant.U2), byteClass, begin);
			for (int i = 0; i < pairs.length; ++i) {
				begin = pairs[i].write(byteClass, begin);
			}
		}
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		typeIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		int numElementValuePairs = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		pairs = new ElementValuePairs[numElementValuePairs];
		for (int i = 0; i < pairs.length; ++i) {
			pairs[i] = new ElementValuePairs();
			begin = pairs[i].read(byteClass, begin);
		}
		return begin;
	}
}
