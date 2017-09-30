package attribute;

import core.Constant;
import utils.ByteUtils;

public class StackMapFrame {
	private int frameType;
	private byte[] offsetDelta;
	private int offset;//这个字段并不是虚拟机规范中标出的字段，而是我为了计算方便添加的字段。表示当前行号的偏移量
	private VerificationTypeInfo[] locals;
	private VerificationTypeInfo[] stack;
	
	public int size() {
		int size = Constant.U1;//frameType
		if (64 <= frameType && frameType <= 127) {
			size += stack[0].size();
		} else if (frameType == 247) {
			size += Constant.U2;//offsetDelta
			size += stack[0].size();
		} else if (248 <= frameType && frameType <= 250) {
			size += Constant.U2;//offsetDelta
		} else if (frameType == 251) {
			size += Constant.U2;//offsetDelta
		} else if (252 <= frameType && frameType <= 254) {
			size += Constant.U2;//offsetDelta
			for (int i = 0; i < locals.length; ++i) {
				size += locals[i].size();
			}
		} else if (frameType == 255)  {
			size += Constant.U2;//offsetDelta
			size += Constant.U2;//numberOfLocals
			for (int i = 0; i < locals.length; ++i) {
				size += locals[i].size();
			}
			size += Constant.U2;//numberOfStackIterms
			for (int i = 0; i < stack.length; ++i) {
				size += stack[i].size();
			}
		} else if (!(0 <= frameType && frameType <= 63)) {
			throw new RuntimeException("不支持frameType为" + frameType + "的StackMapFrame");
		}
		return size;
	}
	
	public int getFrameType() {
		return frameType;
	}
	public void setFrameType(int frameType) {
		this.frameType = frameType;
	}
	public byte[] getOffsetDelta() {
		return offsetDelta;
	}
	public void setOffsetDelta(byte[] offsetDelta) {
		if (offsetDelta == null || offsetDelta.length != Constant.U2) {
			throw new RuntimeException("StackMapFrame的offsetDelta不能为null且长度必须为2字节");
		}
		this.offsetDelta = offsetDelta;
	}
	public VerificationTypeInfo[] getLocals() {
		return locals;
	}
	public void setLocals(VerificationTypeInfo[] locals) {
		this.locals = locals;
	}
	public VerificationTypeInfo[] getStack() {
		return stack;
	}
	public void setStack(VerificationTypeInfo[] stack) {
		this.stack = stack;
	}
	
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)frameType;
		if (64 <= frameType && frameType <= 127) {
			begin = stack[0].write(byteClass, begin);
		} else if (frameType == 247) {
			begin += ByteUtils.copy(offsetDelta, byteClass, begin);
			begin = stack[0].write(byteClass, begin);
		} else if (248 <= frameType && frameType <= 250) {
			begin += ByteUtils.copy(offsetDelta, byteClass, begin);
		} else if (frameType == 251) {
			begin += ByteUtils.copy(offsetDelta, byteClass, begin);
		} else if (252 <= frameType && frameType <= 254) {
			begin += ByteUtils.copy(offsetDelta, byteClass, begin);
			for (int i = 0; i < frameType-251; ++i) {
				begin = locals[i].write(byteClass, begin);
			}
		} else if (frameType == 255)  {
			begin += ByteUtils.copy(offsetDelta, byteClass, begin);
			if (locals == null) {
				byteClass[begin] = byteClass[begin+1] = 0;
				begin += Constant.U2;
			} else {
				begin += ByteUtils.copy(ByteUtils.intToBytes(locals.length, Constant.U2), byteClass, begin);
				for (int i = 0; i < locals.length; ++i) {
					begin = locals[i].write(byteClass, begin);
				}
			}
			
			if (stack == null) {
				byteClass[begin] = byteClass[begin+1] = 0;
				begin += Constant.U2;
			} else {
				begin += ByteUtils.copy(ByteUtils.intToBytes(stack.length, Constant.U2), byteClass, begin);
				for (int i = 0; i < stack.length; ++i) {
					begin = stack[i].write(byteClass, begin);
				}
			}
		} else if (!(0 <= frameType && frameType <= 63)) {
			throw new RuntimeException("不支持frameType为" + frameType + "的StackMapFrame");
		}
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		frameType = byteClass[begin++] & 0xff;
		if (64 <= frameType && frameType <= 127) {
			stack = new VerificationTypeInfo[1];
			stack[0] = new VerificationTypeInfo();
			begin = stack[0].read(byteClass, begin);
		} else if (frameType == 247) {
			offsetDelta = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			stack = new VerificationTypeInfo[1];
			stack[0] = new VerificationTypeInfo();
			begin = stack[0].read(byteClass, begin);
		} else if (248 <= frameType && frameType <= 250) {
			offsetDelta = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		} else if (frameType == 251) {
			offsetDelta = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
		} else if (252 <= frameType && frameType <= 254) {
			offsetDelta = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			locals = new VerificationTypeInfo[frameType - 251];
			for (int i = 0; i < locals.length; ++i) {
				locals[i] = new VerificationTypeInfo();
				begin = locals[i].read(byteClass, begin);
			}
		} else if (frameType == 255)  {
			offsetDelta = ByteUtils.subBytes(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			int numberOfLocals = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			locals = new VerificationTypeInfo[numberOfLocals];
			for (int i = 0; i < numberOfLocals; ++i) {
				locals[i] = new VerificationTypeInfo();
				begin = locals[i].read(byteClass, begin);
			}
			int numbeOfStackItems = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
			begin += Constant.U2;
			stack = new VerificationTypeInfo[numbeOfStackItems];
			for (int i = 0; i < stack.length; ++i) {
				stack[i] = new VerificationTypeInfo();
				begin = stack[i].read(byteClass, begin);
			}
		} else if (!(0 <= frameType && frameType <= 63)) {
			throw new RuntimeException("不支持frameType为" + frameType + "的StackMapFrame");
		}
		return begin;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
