package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import constant.ClassInfo;
import constant.ConstantType;

public class NewCmd implements OpCmd, Opcodes {

	String className;
	
	NewCmd(String className) {
		this.className = className;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		int pos = PoolUtils.addPoolType(pool, poolNum, ClassInfo.class, className);
		codes.add((byte)NEW);
		byte[] bs = ByteUtils.intToBytes(pos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}
}
