package core;


public class TryCatchBlock {
	private Label tryBegin;
	private Label tryEnd;
	private Label catchBegin;
	private String execeptionName;
	TryCatchBlock(Label tryBegin, Label tryEnd, Label catchBegin,
			String execeptionName) {
		this.tryBegin = tryBegin;
		this.tryEnd = tryEnd;
		this.catchBegin = catchBegin;
		this.execeptionName = execeptionName;
	}
	Label getTryBegin() {
		return tryBegin;
	}
	Label getTryEnd() {
		return tryEnd;
	}
	Label getCatchBegin() {
		return catchBegin;
	}
	String getExeceptionName() {
		return execeptionName;
	}
}
