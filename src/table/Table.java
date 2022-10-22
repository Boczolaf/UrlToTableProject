package table;

import exceptions.TableCreationException;
import types.SimpleTypesEnum;
import utility.DataTypeMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Table {

    private static final String SPECIAL_CHARACTERS_REGEX = "[^a-zA-Z0-9]";
    private final ArrayList<String[]> dataList = new ArrayList<>();
    private final String[] dataTypes;
    private final String[] columnNames;
    private final String tableName;

    public Table(String link) throws TableCreationException {
        try {
            URL url = new URL(link);
            tableName = getTableNameFromLink(link);
            BufferedReader read = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String firstLine = read.readLine();
            columnNames = firstLine.split(",");
            int length = columnNames.length;
            dataTypes = new String[length];
            SimpleTypesEnum previousType;
            HashMap<String, SimpleTypesEnum> typesMap = new HashMap<>();
            for (SimpleTypesEnum type : SimpleTypesEnum.values()) {
                typesMap.put(type.getSimpleType().getDatabaseDataType(), type);
            }
            String line;
            String[] currentRow;
            while ((line = read.readLine()) != null) {
                currentRow = separateRecords(line, length);
                for (int i = 0; i < length; i++) {
                    if (!Objects.isNull(dataTypes[i])) {
                        previousType = typesMap.get(dataTypes[i]);
                        dataTypes[i] = DataTypeMapper.getType(currentRow[i]).getSimpleType().getDatabaseDataType();
                        if (previousType.ordinal() > typesMap.get(dataTypes[i]).ordinal()) {
                            dataTypes[i] = previousType.getSimpleType().getDatabaseDataType();
                        }
                    } else {
                        dataTypes[i] = DataTypeMapper.getType(currentRow[i]).getSimpleType().getDatabaseDataType();
                    }

                }
                dataList.add(currentRow);
            }
            read.close();

        } catch (MalformedURLException e) {
            System.out.println("Incorrect url: " + e.getMessage());
            throw new TableCreationException(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error occurred while processing file: " + e.getMessage());
            throw new TableCreationException(e.getMessage());
        }
    }

    private String getTableNameFromLink(String link) {
        String fileName = link.substring(link.lastIndexOf('/') + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        fileName = fileName.replaceAll(SPECIAL_CHARACTERS_REGEX, "_");
        return fileName;
    }

    public ArrayList<String[]> getDataList() {
        return dataList;
    }

    public String[] getDataTypes() {
        return dataTypes;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getTableName() {
        return tableName;
    }

    private String[] separateRecords(String singleLine, int intendedLength) throws IOException {
        String[] array = new String[intendedLength];
        int currentRecordIndex = 0;
        StringBuilder currentRecord = new StringBuilder();
        boolean isInsideQuote = false;
        char[] chars = singleLine.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ('\"' == chars[i]) {
                isInsideQuote = !isInsideQuote;
            } else if ('\"' != chars[i] && ',' != chars[i]) {
                currentRecord.append(chars[i]);
            }

            if (',' == chars[i] && !isInsideQuote) {
                if (currentRecordIndex < intendedLength) {
                    array[currentRecordIndex] = currentRecord.toString();
                    currentRecord = new StringBuilder();
                    currentRecordIndex++;
                } else {
                    throw new IOException("Length doesn't match");
                }

            } else if (i == chars.length - 1) {
                array[currentRecordIndex] = currentRecord.toString();
            }
        }
        return array;
    }


}

