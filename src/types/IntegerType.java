package types;

public class IntegerType extends SimpleType {

    @Override
    public String getDatabaseDataType() {
        return "INT";
    }

    @Override
    public boolean isType(String value) {
        int intValue;
        if (value == null || value.equals("")) {
            return false;
        }
        try {
            intValue = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}
