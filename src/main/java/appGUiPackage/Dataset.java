package appGUiPackage;

import java.util.Hashtable;

public class Dataset {

    Hashtable<String,String>  hashIndextable;

    public Dataset(){
        hashIndextable = new Hashtable<String,String>();
    }

    public void addToIndexDataset(String word, String email){
       if (hashIndextable.containsKey(word)) {
           hashIndextable.replace(word, hashIndextable.get(word) + " " + email);
       } else {
           hashIndextable.put(word, email);
       }
    }

}
