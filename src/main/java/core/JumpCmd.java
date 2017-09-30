package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import constant.ConstantType;

public class JumpCmd implements OpCmd, Opcodes {

	private int cmd;
	private Label label;
	private int jumpPos;
	JumpCmd(int cmd, Label label, int jumpPos) {
		this.cmd = cmd;
		this.label = label;
		this.jumpPos = jumpPos;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		codes.add((byte)cmd);
		byte[] bs = ByteUtils.intToBytes(label.getLineNum() - jumpPos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}

}
