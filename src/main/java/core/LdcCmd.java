package core;

import java.util.List;
import java.util.Map;

import utils.PoolUtils;
import constant.ClassInfo;
import constant.ConstantType;
import constant.StringInfo;

public class LdcCmd implements OpCmd, Opcodes {
	
	private Object obj;
	LdcCmd(Object obj) {
		this.obj = obj;
	}
	
	@Override
	public void add(List<Byte> codes, List<ConstantType> pool,
			Map<String, Integer> poolNum) {
		if (obj instanceof String) {
			codes.add((byte)LDC);
			int pos = PoolUtils.addPoolType(pool, poolNum, StringInfo.class, (String)obj);
			codes.add((byte) pos);
		} else if (obj instanceof Type) {
			codes.add((byte)LDC);
			int pos = PoolUtils.addPoolType(pool, poolNum, ClassInfo.class, ((Type)obj).getTypeName());
			codes.add((byte) pos);
		}else if (obj instanceof Integer || obj instanceof Byte ||
				obj instanceof Character ||	obj instanceof Short) {
			codes.add((byte)LDC);
			PoolUtils.addPoolType(pool, poolNum, (Number)obj);
			codes.add((byte)((Number)obj).intValue());
		}
	}
	
}
