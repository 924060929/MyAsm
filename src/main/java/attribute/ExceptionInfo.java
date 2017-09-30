package attribute;

import core.Constant;
import utils.ByteUtils;

public class ExceptionInfo {
	private byte[] startPc;
	private byte[] endPc;
	private byte[] handlerPc;
	private byte[] catchType;
	
	public byte[] getStartPc() {
		return startPc;
	}
	
	public void setStartPc(byte[] startPc) {
		if (startPc == null || startPc.length != Constant.U2) {
			throw new RuntimeException("ExceptionInfo的startPc不能为null且长度必须为2字节");
		}
		this.startPc = startPc;
	}
	
	public byte[] getEndPc() {
		return endPc;
	}
	
	public void setEndPc(byte[] endPc) {
		if (endPc == null || endPc.length != Constant.U2) {
			throw new RuntimeException("ExceptionInfo的endPc不能为null且长度必须为2字节");
		}
		this.endPc = endPc;
	}
	
	public byte[] getHandlerPc() {
		return handlerPc;
	}
	
	public void setHandlerPc(byte[] handlerPc) {
		if (handlerPc == null || handlerPc.length != Constant.U2) {
			throw new RuntimeException("ExceptionInfo的handlerPc不能为null且长度必须为2字节");
		}
		this.handlerPc = handlerPc;
	}
	public byte[] getCatchType() {
		return catchType;
	}
	
	public void setCatchType(byte[] catchType) {
		if (catchType == null || catchType.length != Constant.U2) {
			throw new RuntimeException("ExceptionInfo的catchType不能为null且长度必须为2字节");
		}
		this.catchType = catchType;
	}
	
	public int size() {
		return Constant.U8;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(startPc, byteClass, begin);
		begin += ByteUtils.copy(endPc, byteClass, begin);
		begin += ByteUtils.copy(handlerPc, byteClass, begin);
		begin += ByteUtils.copy(catchType, byteClass, begin);
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		startPc = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		endPc = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		handlerPc = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		catchType = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}
