package core;

public class Label {
	private int lineNum;
	/**
	 * <p>Title: setLineNum</p>
	 * <p>Description: 只能被MethodHelper修改目的地行号</p>
	 * @param lineNum
	 */
	void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	int getLineNum() {
		return lineNum;
	}
}
