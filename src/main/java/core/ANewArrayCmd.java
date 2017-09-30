package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import constant.ClassInfo;
import constant.ConstantType;

public class ANewArrayCmd implements OpCmd, Opcodes {

	String className;
	
	ANewArrayCmd(String className) {
		this.className = className;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		int pos = PoolUtils.addPoolType(pool, poolNum, ClassInfo.class, className);
		codes.add((byte)ANEWARRAY);
		byte[] bs = ByteUtils.intToBytes(pos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}
}
