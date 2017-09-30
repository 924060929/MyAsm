package attribute;


/**
 * <p>Title: AttributeInfoFactory</p>
 * <p>Description: 根据attrType字符串来生成一个属性表对象。有20种属性表类</p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年12月27日 下午3:22:41
 */
public class AttributeInfoFactory {
	public static AbstractAttributeInfo build(String attrType) {
		
		if (attrType.equals(AttrConstant.CODE)) {
			return new CodeAttr();
		} else if (attrType.equals(AttrConstant.CONSTANT_VALUE)) {
			return new ConstantValueAttr();
		} else if (attrType.equals(AttrConstant.DEPRECATED)) {
			return new DeprecatedAttr();
		} else if (attrType.equals(AttrConstant.EXCEPTIONS)) {
			return new ExceptionsAttr();
		} else if (attrType.equals(AttrConstant.ENCLOSING_METHOD)) {
			return new EnclosingMethodAttr();
		} else if (attrType.equals(AttrConstant.INNER_CLASSES)) {
			return new InnerClassesAttr();
		} else if (attrType.equals(AttrConstant.LINE_NUMBER_TABLE)) {
			return new LineNumberTableAttr();
		} else if (attrType.equals(AttrConstant.LOCAL_VARIABLE_TABLE)) {
			return new LocalVariableTableAttr();
		} else if (attrType.equals(AttrConstant.STACK_MAP_TABLE)) {
			return new StackMapTableAttr();
		} else if (attrType.equals(AttrConstant.SIGNATURE)) {
			return new SignatureAttr();
		} else if (attrType.equals(AttrConstant.SOURCE_FILE)) {
			return new SourceFileAttr();
		} else if (attrType.equals(AttrConstant.SOURCE_DEBUG_EXTENSION)) {
			return new SourceDebugExtensionAttr();
		} else if (attrType.equals(AttrConstant.SYNTHETIC)) {
			return new SyntheticAttr();
		} else if (attrType.equals(AttrConstant.LOCAL_VARIABLE_TYPE_TABLE)) {
			return new LocalVariableTypeTableAttr();
		} else if (attrType.equals(AttrConstant.RUNTIME_VISIBLE_ANNOTATIONS)) {
			return new RuntimeVisibleAnnotationsAttr();
		} else if (attrType.equals(AttrConstant.RUNTIME_INVISIBLE_ANNOTATIONS)) {
			return new RuntimeInvisibleAnnotationsAttr();
		} else if (attrType.equals(AttrConstant.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS)) {
			return new RuntimeVisibleParameterAnnotationsAttr();
		} else if (attrType.equals(AttrConstant.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS)) {
			return new RuntimeInvisibleParameterAnnotationsAttr();
		} else if (attrType.equals(AttrConstant.ANNOTATION_DEFAULT)) {
			return new AnnotationDefaultAttr();
		} else if (attrType.equals(AttrConstant.BOOTSTRAP_METHODS)) {
			return new BootstrapMethodsAttr();
		} else {
			throw new RuntimeException("不支持自定义class文件属性" + attrType);
		}
	}
}
