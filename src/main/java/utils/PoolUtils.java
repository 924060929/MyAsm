package utils;

import java.util.List;
import java.util.Map;

import constant.ClassInfo;
import constant.ConstantType;
import constant.FieldrefInfo;
import constant.IntegerInfo;
import constant.InterfaceMethodrefInfo;
import constant.LongInfo;
import constant.MethodrefInfo;
import constant.NameAndTypeInfo;
import constant.StringInfo;
import constant.Utf8Info;

/**
 * <p>Title: PoolUtils</p>
 * <p>Description: 操作常量池的工具类</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午2:54:17
 */
public class PoolUtils {
	/**
	 * <p>Title: addPoolType</p>
	 * <p>Description: 把一个数字放进常量池中，如果已存在则不重复放入</p>
	 * @param pool
	 * @param poolNum
	 * @param n
	 * @return 该常量在常量池的位置
	 */
	//TODO 未完善
	public static int addPoolType(List<ConstantType> pool, Map<String, Integer> poolNum, Number n) {
		if (n instanceof Integer || n instanceof Byte || n instanceof Short) {
			if (poolNum.get("Integer:" + n.intValue()) != null) {
				return poolNum.get("Integer:" + n.intValue());
			} else {
				IntegerInfo i = new IntegerInfo();
				i.setBytes(ByteUtils.intToBytes(n.intValue(), 4));
				poolNum.put("Integer:" + n.intValue(), pool.size());
				pool.add(i);
			}
			return poolNum.get("Integer:" + n.intValue());
		} else if (n instanceof Long) {
			if (poolNum.get("Long:" + n.longValue()) != null) {
				return poolNum.get("Long:" + n.longValue());
			} else {
				LongInfo l = new LongInfo();
				l.setBytes(ByteUtils.longToBytes(n.longValue(), 8));
				poolNum.put("Long:" + n.longValue(), pool.size());
				pool.add(l);
			}
			return poolNum.get("Long:" + n.longValue());
		} else if (n instanceof Double) {
			throw new RuntimeException("暂不支持Double类型");
//			if (poolNum.get("Double:" + n.doubleValue()) != null) {
//				return poolNum.get("Double:" + n.doubleValue());
//			} else {
//				DoubleInfo d = new DoubleInfo();
//				d.setBytes(ByteUtils.intToBytes(n.doubleValue(), 8));
//				poolNum.put("Double:" + n.intValue(), pool.size());
//				pool.add(d);
//			}
		} else if (n instanceof Float) {
			throw new RuntimeException("暂不支持Float类型");
//			if (poolNum.get("Float:" + n.floatValue()) != null) {
//				return poolNum.get("Float:" + n.floatValue());
//			} else {
//				FloatInfo f = new FloatInfo();
//				f.setBytes(ByteUtils.intToBytes(n.floatValue(), 8));
//				poolNum.put("Float:" + n.intValue(), pool.size());
//				pool.add(f);
//			}
		} else {
			throw new IllegalArgumentException("未知的Number类型：" + n.getClass());
		}
	}
	
	/**
	 * <p>Title: addPoolType</p>
	 * <p>Description: 把一个类型为constantClass的常量放到常量池中，如果已存在则不重复放入</p>
	 * @param pool
	 * @param poolNum
	 * @param constantClass
	 * @param params
	 * @return
	 */
	public static int addPoolType(List<ConstantType> pool, Map<String, Integer> poolNum, Class<?> constantClass, String...params) {
		if (Utf8Info.class.equals(constantClass)) {
			if (poolNum.get("Utf8:" + params[0]) == null) {
				Utf8Info u = new Utf8Info();
				u.setBytes(params[0].getBytes());
				poolNum.put("Utf8:" + params[0], pool.size());
				pool.add(u);
			}
			return poolNum.get("Utf8:" + params[0]);
		} else if (ClassInfo.class.equals(constantClass)) {
			if (poolNum.get("Class:" + params[0]) == null) {
				ClassInfo c = new ClassInfo();
				poolNum.put("Class:" + params[0], pool.size());
				pool.add(c);
				int utf8Pos = addPoolType(pool, poolNum, Utf8Info.class, params[0]);
				c.setIndex(ByteUtils.intToBytes(utf8Pos, 2));
			}
			return poolNum.get("Class:" + params[0]);
		} else if (StringInfo.class.equals(constantClass)) {
			if (poolNum.get("String:" + params[0]) == null) {
				StringInfo s = new StringInfo();
				poolNum.put("String:" + params[0], pool.size());
				pool.add(s);
				int utf8Pos = addPoolType(pool, poolNum, Utf8Info.class, params[0]);
				s.setIndex(ByteUtils.intToBytes(utf8Pos, 2));
			}
			return poolNum.get("String:" + params[0]);
		} else if (NameAndTypeInfo.class.equals(constantClass)) {
			if (poolNum.get("NameAndType:" + params[0] + ":" + params[1]) == null) {
				NameAndTypeInfo n = new NameAndTypeInfo();
				poolNum.put("NameAndType:" + params[0] + ":" + params[1], pool.size());
				pool.add(n);
				int namePos = addPoolType(pool, poolNum, Utf8Info.class, params[0]);
				n.setIndex(ByteUtils.intToBytes(namePos, 2));
				int typePos = addPoolType(pool, poolNum, Utf8Info.class, params[1]);
				n.setIndex2(ByteUtils.intToBytes(typePos, 2));
			}
			return poolNum.get("NameAndType:" + params[0] + ":" + params[1]);
		} else if (FieldrefInfo.class.equals(constantClass)) {
			if (poolNum.get("Fieldref:" + params[0] + "." + params[1] + ":" + params[2]) == null) {
				FieldrefInfo f = new FieldrefInfo();
				poolNum.put("Fieldref:" + params[0] + "." + params[1] + ":" + params[2], pool.size());
				pool.add(f);
				int classPos = addPoolType(pool, poolNum, ClassInfo.class, params[0]);
				f.setIndex(ByteUtils.intToBytes(classPos, 2));
				int nameAndTypePos = addPoolType(pool, poolNum, NameAndTypeInfo.class, params[1], params[2]);
				f.setIndex2(ByteUtils.intToBytes(nameAndTypePos, 2));
			}
			return poolNum.get("Fieldref:" + params[0] + "." + params[1] + ":" + params[2]);
		} else if (MethodrefInfo.class.equals(constantClass)) {
			if (poolNum.get("Methodref:" + params[0] + "." + params[1] + ":" + params[2]) == null) {
				MethodrefInfo m = new MethodrefInfo();
				poolNum.put("Methodref:" + params[0] + "." + params[1] + ":" + params[2], pool.size());
				pool.add(m);
				int classPos = addPoolType(pool, poolNum, ClassInfo.class, params[0]);
				m.setIndex(ByteUtils.intToBytes(classPos, 2));
				int nameAndTypePos = addPoolType(pool, poolNum, NameAndTypeInfo.class, params[1], params[2]);
				m.setIndex2(ByteUtils.intToBytes(nameAndTypePos, 2));
			}
			return poolNum.get("Methodref:" + params[0] + "." + params[1] + ":" + params[2]);
		} else if (InterfaceMethodrefInfo.class.equals(constantClass)) {
			if (poolNum.get("InterfaceMethodref:" + params[0] + "." + params[1] + ":" + params[2]) == null) {
				InterfaceMethodrefInfo m = new InterfaceMethodrefInfo();
				poolNum.put("InterfaceMethodref:" + params[0] + "." + params[1] + ":" + params[2], pool.size());
				pool.add(m);
				int classPos = addPoolType(pool, poolNum, ClassInfo.class, params[0]);
				m.setIndex(ByteUtils.intToBytes(classPos, 2));
				int nameAndTypePos = addPoolType(pool, poolNum, NameAndTypeInfo.class, params[1], params[2]);
				m.setIndex2(ByteUtils.intToBytes(nameAndTypePos, 2));
			}
			return poolNum.get("InterfaceMethodref:" + params[0] + "." + params[1] + ":" + params[2]);
		}
		return -1;
	}
}
