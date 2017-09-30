package attribute;

import core.Constant;
import utils.ByteUtils;

public class LineNumberInfo {
	private byte[] startPc;
	private byte[] lineNumber;
	
	public byte[] getStartPc() {
		return startPc;
	}
	
	public void setStartPc(byte[] startPc) {
		this.startPc = startPc;
	}
	
	public byte[] getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(byte[] lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public int size() {
		return Constant.U4;
	}
	
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(startPc, byteClass, begin);
		begin += ByteUtils.copy(lineNumber, byteClass, begin);
		
		return begin;
	}
	
	public int read(byte[] byteClass, int begin) {
		startPc = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		lineNumber = ByteUtils.subBytes(byteClass, begin, Constant.U2);

		return begin + Constant.U2;
	}
}
