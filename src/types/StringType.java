package types;

public class StringType extends SimpleType {
    @Override
    public String getDatabaseDataType() {
        return "VARCHAR(255)";
    }

    @Override
    public boolean isType(String value) {
        return true;
    }
}
