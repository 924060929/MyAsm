package core;

public interface Opcodes {
	/***jdk版本***/
	public static final int V1_1 = 0x2d;
	public static final int V1_2 = 0x2e;
	public static final int V1_3 = 0x2f;
	public static final int V1_4 = 0x30;
	public static final int V1_5 = 0x31;
	public static final int V1_6 = 0x32;
	public static final int V1_7 = 0x33;
	public static final int V1_8 = 0x34;
	
	/***栈映射帧类型***/
	public static final int SAME_FRAME = 0;
	public static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
	public static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
	public static final int CHOP_FRAME = 248;
	public static final int SAME_FRAME_EXTENDED = 251;
	public static final int APPEND_FRAME = 252;
	public static final int FULL_FRAME = 255;
	
	/***访问权限***/
	public static final int ACC_PUBLIC = 0x0001;//字段、方法、类
	public static final int ACC_PRIVATE = 0x0002;//字段、方法
	public static final int ACC_PROTECTED = 0x0004;//字段、方法
	public static final int ACC_STATIC = 0x0008;//字段、方法
	public static final int ACC_FINAL = 0x0010;//字段、方法、类
	public static final int ACC_SYNCHRONIZED = 0x0020;//方法
	public static final int ACC_SUPER = 0x0020;//类
	public static final int ACC_VOLATILE = 0x0040;//字段
	public static final int ACC_BRIDGE = 0x0040;//方法
	public static final int ACC_TRANSIENT = 0x0080;//字段
	public static final int ACC_VARARGS = 0x0080;//方法
	public static final int ACC_NATIVE = 0x0100;//方法
	public static final int ACC_ABSTRACT = 0x0400;//方法
	public static final int ACC_STRICT = 0x0800;//方法
	public static final int ACC_SYNTHETIC = 0x1000;//字段、方法
	public static final int ACC_ENUM = 0x4000;//字段
	
	/***常量指令***/
	public static final int NOP = 0x00;
	public static final int ACOUNST_NULL = 0X01;
	public static final int ICOUNST_M1 = 0x02;
	public static final int ICOUNT_0 = 0x03;
	public static final int ICOUNT_1 = 0x04;
	public static final int ICOUNT_2 = 0x05;
	public static final int ICOUNT_3 = 0x06;
	public static final int ICOUNT_4 = 0x07;
	public static final int ICOUNT_5 = 0x08;
	public static final int LDC = 0x12;
	
	/***加载指令***/
	public static final int ILOAD = 0x15;
	public static final int LLOAD = 0x16;
	public static final int FLOAD = 0x17;
	public static final int DLOAD = 0x18;
	public static final int ALOAD = 0x19;
	public static final int LLOAD_0 = 0x1e;
	public static final int ALOAD_0 = 0x2a;
	public static final int ALOAD_1 = 0x2b;
	public static final int IALOAD = 0x2e;
	public static final int LALOAD = 0x2f;
	public static final int FALOAD = 0x30;
	public static final int DALOAD = 0x31;
	public static final int AALOAD = 0x32;
	public static final int BALOAD = 0x33;
	public static final int CALOAD = 0x34;
	public static final int SALOAD = 0x35;
	
	/***存储指令***/
	public static final int ISTORE = 0x36;
	public static final int LSTORE = 0x37;
	public static final int FSTORE = 0x38;
	public static final int DSTORE = 0x39;
	public static final int ASTORE = 0x3a;
	public static final int ASTORE_0 = 0x4b;
	public static final int ASTORE_1 = 0x4c;
	public static final int IASTORE = 0x4f;
	public static final int LASTORE = 0x50;
	public static final int FASTORE = 0x51;
	public static final int DASTORE = 0x52;
	public static final int AASTORE = 0x53;
	public static final int BASTORE = 0x54;
	public static final int CASTORE = 0x55;
	public static final int SASTORE = 0x56;
	
	
	/***栈指令***/
	public static final int POP = 0x57;
	public static final int POP2 = 0x58;
	public static final int DUP = 0x59;
	public static final int DUP_X1 = 0x5a;
	public static final int DUP_X2 = 0x5b;
	public static final int DUP2 = 0x5c;
	
	/***比较指令***/
	public static final int LCMP = 0x94;
	public static final int FCMPL = 0x95;
	public static final int FCMPG = 0x96;
	public static final int DCMPL = 0x97;
	public static final int DCMPG = 0x98;
	public static final int IFEQ = 0x99;
	public static final int IFNE = 0x9a;
	public static final int IFLT = 0x9b;
	public static final int IFGE = 0x9c;
	public static final int IFGT = 0x9d;
	public static final int IFLE = 0x9e;
	public static final int IF_ICMPEQ = 0x9f;
	public static final int IF_ICMPNE = 0xa0;
	public static final int IF_ICMPLT = 0xa1;
	public static final int IF_ICMPGE = 0xa2;
	public static final int IF_ICMPGT = 0xa3;
	public static final int IF_ICMPLE = 0xa4;
	public static final int IF_ACMPEQ = 0xa5;
	public static final int IF_ACMPNE = 0xa6;
	
	/***跳转指令***/
	public static final int GOTO = 0xa7;
	
	/***控制指令***/
	public static final int IRETURN = 0xac;
	public static final int LRETURN = 0xad;
	public static final int FRETURN = 0xae;
	public static final int DRETURN = 0xaf;
	public static final int ARETURN = 0xb0;
	public static final int RETURN = 0xb1;
	
	/***引用指令***/
	public static final int GETSTATIC = 0xb2;
	public static final int PUTSTATIC = 0xb3;
	public static final int GETFIELD = 0xb4;
	public static final int PUTFIELD = 0xb5;
	public static final int INVOKEVIRTUAL = 0xb6;
	public static final int INVOKESPECIAL = 0xb7;
	public static final int INVOKESTATIC = 0xb8;
	public static final int INVOKEINTERFACE = 0xb9;
	public static final int NEW = 0xbb;
	public static final int NEWARRAY = 0xbc;
	public static final int ANEWARRAY = 0xbd;
	public static final int ARRAYLENGTH = 0xbe;
	public static final int ATHROW = 0xbf;
	public static final int CHECKCAST = 0xc0;
	public static final int INSTANCEOF = 0xc1;
}
