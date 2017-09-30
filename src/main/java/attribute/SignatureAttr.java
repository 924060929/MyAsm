package attribute;

import constant.ConstantType;
import core.Constant;
import utils.ByteUtils;

public class SignatureAttr extends AbstractAttributeInfo {
	private byte[] signatureIndex;
	
	public byte[] getSignatureIndex() {
		return signatureIndex;
	}
	
	public void setSignatureIndex(byte[] signatureIndex) {
		if (signatureIndex == null || signatureIndex.length != Constant.U2) {
			throw new RuntimeException("SignatureAttr的signatureIndex不能为null且长度必须为2字节");
		}
		this.signatureIndex = signatureIndex;
	}
	
	@Override
	public int getAttributeLength() {
		return Constant.U2;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		begin += ByteUtils.copy(attributeNameIndex, byteClass, begin);
		begin += ByteUtils.copy(ByteUtils.intToBytes(getAttributeLength(), Constant.U4), byteClass, begin);
		begin += ByteUtils.copy(signatureIndex, byteClass, begin);
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin, ConstantType[] pool) {
		begin += Constant.U4;
		signatureIndex = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
	
	@Override
	public int size() {
		int size = Constant.U2 + Constant.U4;//attributeNameIndex、attributeLength
		size += getAttributeLength();
		return size;
	}
}
