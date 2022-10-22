package utility;

import types.SimpleTypesEnum;

public class DataTypeMapper {

    public static SimpleTypesEnum getType(String data) {
        for (SimpleTypesEnum simpleType : SimpleTypesEnum.values()) {
            if (simpleType.getSimpleType().isType(data)) {
                return simpleType;
            }
        }
        return SimpleTypesEnum.STRING;
    }

}
