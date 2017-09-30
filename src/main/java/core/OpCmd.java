package core;

import java.util.List;
import java.util.Map;

import constant.ConstantType;

public interface OpCmd {
	/**
	 * <p>Title: add</p>
	 * <p>Description: 被MethodHelper的end方法调用，
	 * 将临时保存的字节码放到codes数组中去，会自动在常量池中查找、增加需要的常量</p>
	 * @param codes
	 * @param pool
	 * @param poolNum
	 */
	public void add(List<Byte> codes, List<ConstantType> pool, Map<String, Integer> poolNum);
}
