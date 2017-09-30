package core;

import java.util.List;
import java.util.Map;

import utils.ByteUtils;
import utils.PoolUtils;
import constant.ConstantType;
import constant.MethodrefInfo;

public class InvokeMethodCmd implements OpCmd {
	private int cmd;
	private String owner;
	private String methodName;
	private String type;
	InvokeMethodCmd(int cmd, String owner, String methodName, String type) {
		this.cmd = cmd;
		this.owner = owner;
		this.methodName = methodName;
		this.type = type;
	}

	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		int pos = PoolUtils.addPoolType(pool, poolNum, MethodrefInfo.class, owner, methodName, type);
		codes.add((byte)cmd);
		byte[] bs = ByteUtils.intToBytes(pos, 2);
		codes.add(bs[0]);
		codes.add(bs[1]);
	}

}
