package core;

public class Type {
	private String typeName;
	
	private Type(String typeName) {
		this.typeName = typeName;
	}
	
	public static Type getType(String typeName) {
		return new Type(typeName);
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	@Override
	public String toString() {
		return typeName;
	}
}	
