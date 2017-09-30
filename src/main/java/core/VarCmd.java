package core;

import java.util.List;
import java.util.Map;

import constant.ConstantType;

public class VarCmd implements OpCmd, Opcodes {
	private int cmd;
	private int target;
	
	VarCmd(int cmd, int target) {
		this.cmd = cmd;
		this.target = target;
	}

	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		if (cmd == ALOAD && target >= 0 && target < 4) {
			codes.add((byte)(ALOAD_0 + target));
		} else if (cmd == ASTORE && target >= 0 && target < 4) {
			codes.add((byte)(ASTORE_0 + target));
		} else if (cmd == LLOAD && target >= 0 && target < 4) {
			codes.add((byte)(LLOAD_0 + target));
		} else {
			codes.add((byte)cmd);
			codes.add((byte)target);
		}
	}

}
