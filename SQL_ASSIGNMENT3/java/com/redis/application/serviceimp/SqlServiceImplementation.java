package com.redis.application.serviceimp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class SqlServiceImplementation implements SqlService {

	@Override
	  public void executeQuery(String query) {
		
		System.out.println("Received query: " + query);
        if (query.toUpperCase().startsWith("CREATE TABLE")) {
            createTable(query);
        } else if (query.toUpperCase().startsWith("INSERT INTO")) {
            insertIntoTable(query);
        } else {
            System.out.println("Unsupported query. Please enter valid SQL-like queries.");
        }
    }

	@Override
	 public void createTable(String query) {
        // Extracting table name and column definitions using regex
        System.out.println("inside create ");
        Pattern pattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*?)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
      //  System.out.println("inside create ");

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String columnsInfo = matcher.group(2);
            String[] columns = columnsInfo.split(",");

            // Writing metadata to a separate file
            try (FileWriter metadataFile = new FileWriter(tableName + "_metadata.txt")) {
                metadataFile.write(columnsInfo.trim());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Creating a file for the table
            try {
                File tableFile = new File(tableName + ".txt");
                if (tableFile.createNewFile()) {
                    System.out.println("Table '" + tableName + "' created successfully.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


	@Override
    public void insertIntoTable(String query) {
        // Extracting table name and values using regex
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+) VALUES \\((.*?)\\)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String valuesInfo = matcher.group(2);
            String[] values = valuesInfo.split(",");

            // Reading metadata to get column names
            List<String> columns = new ArrayList<>();
            try (BufferedReader metadataFile = new BufferedReader(new FileReader(tableName + "_metadata.txt"))) {
                String line;
                while ((line = metadataFile.readLine()) != null) {
                    String[] columnNames = line.split(",");
                    for (String columnName : columnNames) {
                        columns.add(columnName.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Appending values to the table file
            try (FileWriter tableFile = new FileWriter(tableName + ".txt", true)) {
                StringBuilder rowData = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    String value = values[i].trim();
                    if (columns.get(i).equals("STRING")) {
                        value = "'" + value + "'";
                    }
                    rowData.append(value);
                    if (i != values.length - 1) {
                        rowData.append(",");
                    }
                }
                tableFile.write(rowData.toString() + "\n");
                System.out.println("Values inserted into '" + tableName + "' successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
