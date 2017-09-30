package constant;

public interface ConstantType {
	public int getTag();
	public int size();
	public int write(byte[] byteClass, int begin);
	public int read(byte[] byteClass, int begin);
}
