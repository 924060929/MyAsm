package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import constant.ClassInfo;
import constant.ConstantType;

public class CastCmd implements OpCmd {
	private int cmd;
	private String type;
	CastCmd(int cmd, String type) {
		this.cmd = cmd;
		this.type = type;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool, Map<String, Integer> poolNum) {
		codes.add((byte)cmd);
		int pos = PoolUtils.addPoolType(pool, poolNum, ClassInfo.class, type);
		byte[] bs = ByteUtils.intToBytes(pos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}
	
}
