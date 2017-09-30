package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import constant.ConstantType;
import constant.FieldrefInfo;

public class FieldCmd implements OpCmd {
	private int cmd;
	private String owner;
	private String fieldName;
	private String type;

	FieldCmd(int cmd, String owner, String fieldName, String type) {
		this.cmd = cmd;
		this.owner = owner;
		this.fieldName = fieldName;
		this.type = type;
	}

	public void add(List<Byte> codes, List<ConstantType> pool, Map<String, Integer> poolNum) {
		int fieldPos = PoolUtils.addPoolType(pool, poolNum, FieldrefInfo.class, owner, fieldName, type);
		codes.add((byte)cmd);
		byte[] bs = ByteUtils.intToBytes(fieldPos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}
}
