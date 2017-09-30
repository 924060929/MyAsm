package attribute;

import constant.ConstantType;
import core.Constant;

/**
 * <p>Title: AbstractAttributeInfo</p>
 * <p>Description: 所有的属性表都继承这个类，是对象树中的一员，
 * 被ClassImage、MethodInfo、FieldInfo、AbstractAttributeInfo包装</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午3:21:20
 */
public abstract class AbstractAttributeInfo {
	protected byte[] attributeNameIndex;
	
	public AbstractAttributeInfo() {}
	
	public AbstractAttributeInfo(byte[] attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
	}
	
	public byte[] getAttributeNameIndex() {
		return attributeNameIndex;
	}
	
	public void setAttributeNameIndex(byte[] attributeNameIndex) {
		if (attributeNameIndex == null || attributeNameIndex.length != Constant.U2) {
			throw new RuntimeException("属性表中的attributeNameIndex不能为空且长度必须为2");
		}
		
		this.attributeNameIndex = attributeNameIndex;
	}

	public abstract int size();
	
	public abstract int  getAttributeLength();
	
	public abstract int write(byte[] byteClass, int begin);
	
	public abstract int read(byte[] byteClass, int begin, ConstantType[] pool);
}
