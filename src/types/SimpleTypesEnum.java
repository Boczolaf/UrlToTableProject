package types;

public enum SimpleTypesEnum {
    DOUBLE(new DoubleType()),
    INT(new IntegerType()),
    STRING(new StringType());

    private final SimpleType simpleType;

    SimpleTypesEnum(SimpleType simpleType) {
        this.simpleType = simpleType;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }
}
