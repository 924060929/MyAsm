package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import attribute.AbstractAttributeInfo;
import attribute.CodeAttr;
import attribute.ExceptionInfo;
import attribute.StackMapFrame;
import attribute.StackMapTableAttr;
import attribute.VerificationTypeInfo;
import constant.ClassInfo;
import constant.ConstantType;
import constant.Utf8Info;

/**
 * <p>Title: MethodHelper</p>
 * <p>Description: 用来为方法添加字节码指令，
 * 能够自动把需要的常量放进常量池中，还能自动计算跳转指令和栈映射帧的偏移量。</p>
 * <p>Company: null</p> 
 * @Note <b>这个类还有很多地方未完善，比如没有检验字节码指令是否合法、不支持某些类型的字节码指令</b>
 * @author simon
 * @date 2014年12月27日 下午1:39:38
 */
//TODO 完善此类，加上字节码校验
public class MethodHelper implements Opcodes {
	private List<Byte> codes;//字节码指令,等到调用end方法时，再统一把cmds转换成byte
	private List<OpCmd> cmds;//字节码指令的自定义包装类
	private int maxLocals;
	private int maxStack;
	private int access;//方法权限
	private int linenum;//用来计算跳转指令、栈映射帧的偏移量
	private String name;
	private String type;//方法的描述符
	private List<ConstantType> pool;
	private Map<String, Integer> poolNum;
	private List<TryCatchBlock> exceptionTable;
	private List<StackMapFrame> frames;//栈映射帧
	private MethodInfo method;
	private ClassHelper classHelper;
	
	MethodHelper(ClassHelper classHelper, String className, List<ConstantType> pool, Map<String, Integer> poolNum, int access, String name, String type) {
		this.classHelper = classHelper;
		codes = new ArrayList<Byte>();
		cmds = new ArrayList<OpCmd>();
		this.access = access;
		this.name = name;
		this.type = type;
		this.pool = pool;
		this.poolNum = poolNum;
	}
	
	/**
	 * <p>Title: addCode</p>
	 * <p>Description: 添加无操作数指令</p>
	 * @param code
	 */
	public void addCode(int code) {
		cmds.add(new DefaultCmd(code));
		linenum++;
	}
	
	/**
	 * <p>Title: ldc</p>
	 * <p>Description: 把数字/Class、String放到操作数栈顶</p>
	 * @param target 可以是String、基本类型的包装类型和Type，用Type.getType(String classname)来获得Type从而表示Class。
	 * @Node<b>由于时间问题，支持的包装类型只有Integer</b>
	 */
	public void ldc(Object target) {
		if (target instanceof Integer) {
			int i = (Integer)target;
			if (-1 <= i && i < 6) {//0~5的整数可以用指令ICOUNT_0~ICOUNT_5直接表示，不需要操作数
				cmds.add(new DefaultCmd(ICOUNT_0 + i));
				linenum++;
			}
		} else {
			cmds.add(new LdcCmd(target));
			linenum += 2;
		}
	}
	
	/**
	 * <p>Title: field</p>
	 * <p>Description: 保存或得到字段</p>
	 * @param code 字节码指令，一般有Opcodes.GETSTATIC、
	 * 		Opcodes.PUTSTATIC、Opcodes.GETFIELD、Opcodes.PUTFIELD
	 * @param owner 字段的拥有者
	 * @param fieldName 字段名
	 * @param type 描述符
	 */
	public void field(int code, String owner, String fieldName, String type) {
		cmds.add(new FieldCmd(code, owner, fieldName, type));
		linenum += 3;
	}
	
	/**
	 * <p>Title: var</p>
	 * <p>Description: 将局部变量放到操作数栈栈顶或着反过来</p>
	 * @param code
	 * @param localNum
	 */
	public void var(int code, int localNum) {
		if (code == ALOAD && localNum >= 0 && localNum < 4) {
			cmds.add(new VarCmd(ALOAD, localNum));
			linenum++;
		} else if (code == ASTORE && localNum >= 0 && localNum < 4) {
			cmds.add(new VarCmd(ASTORE, localNum));
			linenum++;
		} else if (code == LLOAD && localNum >= 0 && localNum < 4) {
			cmds.add(new VarCmd(LLOAD, localNum));
			linenum++;
		} else {
			cmds.add(new VarCmd(code, localNum));
			linenum += 2;
		}
	}
	
	/**
	 * <p>Title: cast</p>
	 * <p>Description: 强转成type类型</p>
	 * @param type
	 */
	public void cast(String type) {
		cmds.add(new CastCmd(CHECKCAST, type));
		linenum += 3;
	}
	
	/**
	 * <p>Title: newObj</p>
	 * <p>Description: 实例化className类型</p>
	 * @param className
	 */
	public void newObj(String className) {
		cmds.add(new NewCmd(className));
		linenum += 3;
	}
	
	/**
	 * <p>Title: aNewArray</p>
	 * <p>Description: 创建一个className引用类型的数组</p>
	 * @param className
	 */
	public void aNewArray(String className) {
		cmds.add(new ANewArrayCmd(className));
		linenum += 3;
	}
	
	
	/**
	 * <p>Title: invokeMethod</p>
	 * <p>Description: 调用方法</p>
	 * @param code 字节码指令，一般有Opcodes.INVOKEVIRTUAL、
	 * Opcodes.INVOKESPECIAL、Opcodes.INVOKESTATIC、Opcodes.INVOKEINTERFACE
	 * @param className 方法所属的类
	 * @param methodName 方法名
	 * @param type 描述符
	 */
	public void invokeMethod(int code, String className, String methodName, String type) {
		cmds.add(new InvokeMethodCmd(code, className, methodName, type));
		linenum += 3;
	}
	

	/**
	 * <p>Title: jump</p>
	 * <p>Description: 跳转到label处，自动计算偏移量</p>
	 * @param code 一般用Opcodes.GOTO,还有一些比较跳转指令
	 * @param label 
	 */
	public void jump(int code, Label label) {
		cmds.add(new JumpCmd(code, label, linenum));
		linenum += 3;
	}
	
	/**
	 * <p>Title: setMaxs</p>
	 * <p>Description: 设置最大局部变量的大小和最大操作数栈的大小</p>
	 * @param locals
	 * @param stack
	 */
	public void setMaxs(int locals, int stack) {
		this.maxLocals = locals;
		this.maxStack = stack;
	}
	
	/**
	 * <p>Title: addLabel</p>
	 * <p>Description: 让下一条字节码指令作为label的跳转目的地。
	 * 在本方法后一般会调用addFrame方法，但是addFrame方法不算下一条字节码指令</p>
	 * @param label
	 */
	public void addLabel(Label label) {
		label.setLineNum(linenum);
	}
	
	/**
	 * <p>Title: tryCatch</p>
	 * <p>Description: 添加一个try-catch代码块</p>
	 * @param tryBegin
	 * @param tryEnd
	 * @param catchBegin
	 * @param exceptionName
	 */
	public void tryCatch(Label tryBegin, Label tryEnd, Label catchBegin, String exceptionName) {
		if (exceptionTable == null) {
			exceptionTable = new ArrayList<TryCatchBlock>();
		}
		exceptionTable.add(new TryCatchBlock(tryBegin, tryEnd, catchBegin, exceptionName));
	}
	
	/**
	 * <p>Title: calculateFrame</p>
	 * <p>Description: 计算并设置栈映射帧的offsetDelta</p>
	 * @param frame
	 * @param frameType
	 */
	private void calculateFrame(StackMapFrame frame, int frameType) {
		if (frames.size() == 0) {//如果是第一个非默认的栈映射帧
			if (frameType == SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED ||
					frameType == SAME_FRAME_EXTENDED ||
					frameType == FULL_FRAME) {
				frame.setFrameType(frameType);//这三种核查类型的类型号是固定的
			} else {
				//第一个栈映射帧(非默认的)的偏移量就是下一条字节码指令的行号加上偏移量为0时的核查类型的类型号
				//因此frameType应该是偏移量为0时的核查类型的类型号
				//比如CHOP_FRAME核查类型的类型号是248~250，偏移量为0时，核查类型是248
				frame.setFrameType(frameType + linenum);
			}
			frame.setOffsetDelta(ByteUtils.intToBytes(linenum, 2));
		} else {
			int lastOffset = frames.get(frames.size()-1).getOffset();//上一个核查类型的代码偏移量
			int offsetDelta = linenum - 1 - lastOffset;//由代码偏移量=上一个核查类型的代码偏移量+offsetDelta+1推得
			frame.setOffsetDelta(ByteUtils.intToBytes(offsetDelta, 2));
			if (frameType == SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED ||
					frameType == SAME_FRAME_EXTENDED ||
					frameType == FULL_FRAME) {
				frame.setFrameType(frameType);
			} else {
				frame.setFrameType(frameType + offsetDelta);
			}
		}
	}
	
	/**
	 * <p>Title: getVerificationType</p>
	 * <p>Description: 获得局部变量表或操作数栈的核查类型数组</p>
	 * @param types Class的全限定名数组
	 * @return
	 */
	// TODO Long、Double核查类型之后应该是Top核查类型，还需要处理
	private VerificationTypeInfo[] getVerificationType(Object[] types) {
		if (types != null && types.length > 0) {
			VerificationTypeInfo[] vTypes = null;
			vTypes = new VerificationTypeInfo[types.length];
			
			for (int i = 0; i < vTypes.length; ++i) {
				VerificationTypeInfo v = new VerificationTypeInfo();
				vTypes[i] = v;
				if (types[i] instanceof String) {
					if ("int".equals(types[i]) ||
						"byte".equals(types[i]) ||
						"char".equals(types[i]) ||
						"boolean".equals(types[i])||
						"short".equals(types[i])) {
						v.setTag((byte)1);//核查类型为
					} else if ("float".equals(types[i])) {
						v.setTag((byte)2);
					} else if ("double".equals(types[i])) {
						v.setTag((byte)3);
					} else if ("long".equals(types[i])) {
						v.setTag((byte)4);
					}  else if ("null".equals(types[i])) {
						v.setTag((byte)5);
					} else {
						v.setTag((byte)7);
						int pos = PoolUtils.addPoolType(pool, poolNum, ClassInfo.class, (String)types[i]);
						v.setCpoolIndex(ByteUtils.intToBytes(pos, 2));
					}
				}
			}
			return vTypes;
		}
		return null;
	}
	
	/**
	 * <p>Title: addFrame</p>
	 * <p>Description: 增加一个栈映射帧，一般在addLabel方法之后调用</p>
	 * @param frameType
	 * @param locals
	 * @param stack
	 */
	public void addFrame(int frameType, Object[] locals, Object[] stack) {
		if (frames == null) {
			frames = new ArrayList<StackMapFrame>();
			PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, "StackMapTable");
		}
		StackMapFrame frame = new StackMapFrame();
		frame.setOffset(linenum);
		calculateFrame(frame, frameType);
		frame.setLocals(getVerificationType(locals));
		frame.setStack(getVerificationType(stack));
	}
	
	/**
	 * <p>Title: addCodeAttr</p>
	 * <p>Description: 由end方法调用，为本方法添加Code属性表</p>
	 */
	private void addCodeAttr() {
		CodeAttr codeAttr = new CodeAttr();
		int codePos = PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, "Code");
		codeAttr.setAttributeNameIndex(ByteUtils.intToBytes(codePos, 2));
		codeAttr.setMaxLocals(ByteUtils.intToBytes(maxLocals, 2));
		codeAttr.setMaxStack(ByteUtils.intToBytes(maxStack, 2));
		for (int i = 0; i < cmds.size(); ++i) {
			OpCmd cmd = cmds.get(i);
			cmd.add(codes, pool, poolNum);//将cmds转换成byte
		}
		byte[] byteCodes = new byte[this.codes.size()];
		for (int i = 0; i < byteCodes.length; ++i) {//把ArrayList的字节码指令转换成数组
			byteCodes[i] = this.codes.get(i);
		}
		AbstractAttributeInfo[] methodAttrs = {codeAttr};
		method.setAttributes(methodAttrs);
		codeAttr.setCodes(byteCodes);

		if (exceptionTable != null) {//添加异常表
			ExceptionInfo[] exTable = new ExceptionInfo[exceptionTable.size()];
			for (int i = 0; i < exTable.length; ++i) {
				ExceptionInfo ex = new ExceptionInfo();
				ex.setStartPc(ByteUtils.intToBytes(exceptionTable.get(i).getTryBegin().getLineNum(), 2));
				ex.setEndPc(ByteUtils.intToBytes(exceptionTable.get(i).getTryEnd().getLineNum(), 2));
				ex.setHandlerPc(ByteUtils.intToBytes(exceptionTable.get(i).getCatchBegin().getLineNum(), 2));
				ex.setCatchType(ByteUtils.intToBytes(poolNum.get("Class:" + exceptionTable.get(i).getExeceptionName()), 2));
				exTable[i] = ex;
			}
			codeAttr.setExceptionTable(exTable);
		}
	}
	
	/**
	 * <p>Title: addStackMapFrame</p>
	 * <p>Description: 由end方法调用。如果添加了栈映射帧就添加到对象树中</p>
	 */
	private void addStackMapFrame() {
		if (frames != null) {
			CodeAttr codeAttr = (CodeAttr)method.getAttributes()[0];
			StackMapTableAttr stackMapTable = new StackMapTableAttr();
			int stackMapTableStr = PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, "StackMapTable");
			stackMapTable.setAttributeNameIndex(ByteUtils.intToBytes(stackMapTableStr, 2));
			StackMapFrame[] framesArr = new StackMapFrame[frames.size()]; 
			stackMapTable.setEntries(frames.toArray(framesArr));
			AbstractAttributeInfo[] codeAttrs = {stackMapTable};
			codeAttr.setAttributes(codeAttrs);
		}
	}
	
	/**
	 * <p>Title: end</p>
	 * <p>Description: 完成方法的生成。将临时保存的字节码指令、异常表、栈映射帧等数据加入到对象树中</p>
	 */
	public void end() {
		method = new MethodInfo();
		method.setAccessFlags(ByteUtils.intToBytes(access, 2));
		int methodNamePos = PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, name);
		PoolUtils.addPoolType(pool, poolNum, Utf8Info.class, type);
		method.setNameIndex(ByteUtils.intToBytes(methodNamePos, 2));
		method.setDescriptorIndex(ByteUtils.intToBytes(poolNum.get("Utf8:" + type), 2));
		
		addCodeAttr();
		addStackMapFrame();
		
		classHelper.addMethod(method);
	}
}
