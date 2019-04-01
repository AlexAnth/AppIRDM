package appGUiPackage;

import java.io.*;
import java.net.URL;
import java.util.*;


public class BooleanIR {

    private static URL pathFolder = BooleanIR.class.getResource("TrainData");
    private static SQLi database;
    private static ArrayList<String> words = new ArrayList<String>();

    public BooleanIR() throws IOException {
        database = new SQLi();
        SQLi.createIndexTable();
        SQLi.createBooleanTable();

        //insertDataset(pathFolder.getPath()+"\\");

    }

    public static SQLi getDatabase() {
        return database;
    }

    private static void insertDataset(String pathFolder) throws IOException {
        File folder = new File(pathFolder);
        File[] listOfFiles = folder.listFiles();        //files in folder
        MailReader reader = new MailReader();
        // create hashtables
        Dataset d = new Dataset();
        for (File file : listOfFiles) {
            for ( String word : reader.loadMail(pathFolder + file.getName())){
                        d.addToIndexDataset(word, file.getName());
                        words.add(word);
            }
        }

        System.out.println(listOfFiles.length*words.size()+" number of records");

        // Boolean table initialization
        for (File file : listOfFiles) {
            System.out.println("new mail\n");
            for (String word : words) {
                database.insertBooleanTable(word, file.getName(), 0);
            }
        }

        System.out.println("Boolean table initialized\n");

        // Set the index database table and update values of boolean table
        for (String name: d.hashIndextable.keySet()){
            String value = d.hashIndextable.get(name);
            List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
            database.insertIndexTable(name,value);
            for(String i : items){
                database.insertBooleanTable(name,i,1);
            }
        }

        System.out.println("Database loaded");
    }




}




