package core;

import java.util.List;
import java.util.Map;

import constant.ConstantType;

public class DefaultCmd implements OpCmd {
	private int cmd;
	DefaultCmd(int cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool, Map<String, Integer> poolNum) {
		codes.add((byte)cmd);
	}
	
}
