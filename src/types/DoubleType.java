package types;

public class DoubleType extends SimpleType {

    @Override
    public String getDatabaseDataType() {
        return "DOUBLE";
    }

    @Override
    public boolean isType(String value) {
        double doubleValue;
        if (value == null || value.equals("") || value.split("\\.").length == 0) {
            return false;
        }
        try {
            doubleValue = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}
