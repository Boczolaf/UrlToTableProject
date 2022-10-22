package types;

public abstract class SimpleType {
    public abstract String getDatabaseDataType();

    public abstract boolean isType(String value);
}
