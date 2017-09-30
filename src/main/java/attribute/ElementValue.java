package attribute;

import core.Constant;
import utils.ByteUtils;

public class ElementValue {
	private byte tag;
	private byte[] constValueIndex;
	private EnumConstValue enumConstValue;
	private byte[] classInfoIndex;
	private Annotation annotationValue;
	private ArrayValue arrayValue;

	public int size() {
		int size = Constant.U1;
		if (tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F'
				|| tag == 'I' || tag == 'J' || tag == 'S' || tag == 'Z'
				|| tag == 's') {
			size += Constant.U2;
		} else if (tag == 'e') {
			size += enumConstValue.size();
		} else if (tag == 'c') {
			size += Constant.U2;
		} else if (tag == '@') {
			size += annotationValue.size();
		} else if (tag == '[') {
			size += arrayValue.size();
		} else {
			throw new RuntimeException("不支持类型：" + (char)tag);
		}
		return size;
	}
	
	public static class EnumConstValue {
		private byte[] typeNameIndex;
		private byte[] constNameIndex;
		
		public int size() {
			return Constant.U4;
		}
		public byte[] getTypeNameIndex() {
			return typeNameIndex;
		}
		public void setTypeNameIndex(byte[] typeNameIndex) {
			if (typeNameIndex == null || typeNameIndex.length != Constant.U2) {
				throw new RuntimeException("EnumConstValue的typeNameIndex不能为null且长度必须为2字节");
			}
			this.typeNameIndex = typeNameIndex;
		}
		public byte[] getConstNameIndex() {
			return constNameIndex;
		}
		public void setConstNameIndex(byte[] constNameIndex) {
			if (constNameIndex == null || constNameIndex.length != Constant.U2) {
				throw new RuntimeException("EnumConstValue的constNameIndex不能为null且长度必须为2字节");
			}
			this.constNameIndex = constNameIndex;
		}
		public int write(byte[] byteClass, int begin) {
			begin += ByteUtils.copy(typeNameIndex, byteClass, begin);
			begin += ByteUtils.copy(constNameIndex, byteClass, begin);
			return begin;
		}
		
		public int read(byte[] byteClass, int begin) {
			typeNameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			constNameIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			return begin + Constant.U2;
		}
	}
	public static class ArrayValue {
		private ElementValue[] values;
		
		public int size() {
			int size = Constant.U2;//numValues
			if (values != null) {
				for (int i = 0; i < values.length; ++i) {
					size += values[i].size();
				}
			}
			return size;
		}

		public ElementValue[] getValues() {
			return values;
		}

		public void setValues(ElementValue[] values) {
			this.values = values;
		}
		public int write(byte[] byteClass, int begin) {
			if (values == null) {
				byteClass[begin] = byteClass[begin+1] = 0;
				begin += Constant.U2;
			} else {
				begin += ByteUtils.copy(ByteUtils.intToBytes(values.length, Constant.U2), byteClass, begin);
				for (int i = 0; i < values.length; ++i) {
					begin = values[i].write(byteClass, begin);
				}
			}
			return begin;
		}
		
		public int read(byte[] byteClass, int begin) {
			int numValues = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			values = new ElementValue[numValues];
			for (int i = 0; i < values.length; ++i) {
				values[i] = new ElementValue();
				begin = values[i].read(byteClass, begin);
			}
			return begin;
		}
	}
	public byte getTag() {
		return tag;
	}
	public void setTag(byte tag) {
		this.tag = tag;
	}
	public byte[] getConstValueIndex() {
		return constValueIndex;
	}
	public void setConstValueIndex(byte[] constValueIndex) {
		if (constValueIndex == null || constValueIndex.length != Constant.U2) {
			throw new RuntimeException("ElementValue的constValueIndex不能为null且长度必须为2字节");
		}
		this.constValueIndex = constValueIndex;
	}

	public EnumConstValue getEnumConstValue() {
		return enumConstValue;
	}

	public void setEnumConstValue(EnumConstValue enumConstValue) {
		this.enumConstValue = enumConstValue;
	}

	public byte[] getClassInfoIndex() {
		return classInfoIndex;
	}

	public void setClassInfoIndex(byte[] classInfoIndex) {
		if (classInfoIndex == null || classInfoIndex.length != Constant.U2) {
			throw new RuntimeException("ElementValue的classInfoIndex不能为null且长度必须为2字节");
		}
		this.classInfoIndex = classInfoIndex;
	}

	public Annotation getAnnotationValue() {
		return annotationValue;
	}

	public void setAnnotationValue(Annotation annotationValue) {
		this.annotationValue = annotationValue;
	}

	public ArrayValue getArrayValue() {
		return arrayValue;
	}

	public void setArrayValue(ArrayValue arrayValue) {
		this.arrayValue = arrayValue;
	}
	public static void main(String[] args) {
		System.out.println((int)'z');
	}
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = tag;
		if (tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F'
				|| tag == 'I' || tag == 'J' || tag == 'S' || tag == 'Z'
				|| tag == 's') {
			begin += ByteUtils.copy(constValueIndex, byteClass, begin);
		} else if (tag == 'e') {
			begin = enumConstValue.write(byteClass, begin);
		} else if (tag == 'c') {
			begin += ByteUtils.copy(classInfoIndex, byteClass, begin);
		} else if (tag == '@') {
			begin = annotationValue.write(byteClass, begin);
		} else if (tag == '[') {
			begin = arrayValue.write(byteClass, begin);
		} else {
			throw new RuntimeException("不支持类型：" + (char)tag);
		}
		
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		tag = byteClass[begin];
		begin += Constant.U1;
		if (tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F'
				|| tag == 'I' || tag == 'J' || tag == 'S' || tag == 'Z'
				|| tag == 's') {
			constValueIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		} else if (tag == 'e') {
			enumConstValue = new EnumConstValue();
			begin = enumConstValue.read(byteClass, begin);
		} else if (tag == 'c') {
			classInfoIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		} else if (tag == '@') {
			annotationValue = new Annotation();
			begin = annotationValue.read(byteClass, begin);
		} else if (tag == '[') {
			arrayValue = new ArrayValue();
			begin = arrayValue.read(byteClass, begin);
		} else {
			throw new RuntimeException("不支持类型：" + (char)tag);
		}
			
		return begin;
	}
}
