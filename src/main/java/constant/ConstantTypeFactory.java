package constant;

import core.ConstantPoolTag;

public class ConstantTypeFactory {
	public static ConstantType build(byte tag) {
		ConstantType c = null;
		switch(tag) {
		case ConstantPoolTag.CONSTANT_Utf8_info: c = new Utf8Info(); break;
		case ConstantPoolTag.CONSTANT_Integer_info: c = new IntegerInfo(); break;
		case ConstantPoolTag.CONSTANT_Float_info: c = new FloatInfo(); break;
		case ConstantPoolTag.CONSTANT_Long_info: c = new LongInfo(); break;
		case ConstantPoolTag.CONSTANT_Double_info: c = new DoubleInfo(); break;
		case ConstantPoolTag.CONSTANT_Class_info: c = new ClassInfo(); break;
		case ConstantPoolTag.CONSTANT_String_info: c = new StringInfo(); break;
		case ConstantPoolTag.CONSTANT_Fieldref_info: c = new FieldrefInfo(); break;
		case ConstantPoolTag.CONSTANT_Methodref_info: c = new MethodrefInfo(); break;
		case ConstantPoolTag.CONSTANT_InterfaceMethodref_info: c = new InterfaceMethodrefInfo(); break;
		case ConstantPoolTag.CONSTANT_NameAndType_info: c = new NameAndTypeInfo(); break;
		case ConstantPoolTag.CONSTANT_MethodHandle_info: c = new MethodHandleInfo(); break;
		case ConstantPoolTag.CONSTANT_MethodType_info: c = new MethodTypeInfo(); break;
		case ConstantPoolTag.CONSTANT_InvokeDynamic_info: c = new InvokeDynamicInfo(); break;
		default: c = null;
		}
		
		return c;
	}
}
