package appGUiPackage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MailReader {
	
	private URL pathStopwords = MailReader.class.getResource("/stop-word-list.txt");

	public String[] loadMail(String path) throws IOException{

		String[] wordsfinal;
		List<String> sw = setStopwords();
		BufferedReader br = new BufferedReader(new FileReader(path));
		boolean firstIter= true;
		String st;
		List<String> list=new ArrayList<String>();												//read line by line

		while ((st = br.readLine()) != null) {
		    st = st.replaceAll("[^a-zA-Z ]", "");									//remove non-letters from string
		    st= st.toLowerCase();															//only lowercase letters
		    st = st.trim().replaceAll(" +", " ");									//remove multiple spaces
			String[] words=st.split(" ");				 								//split words in words array
			words = removeStopwords(sw,words);
			if(firstIter) {
				list = new ArrayList<String>(Arrays.asList(words));							//array to list
				firstIter = false;
			}else{
				list.addAll(Arrays.asList(words));											//add array/s to list
			}

		}
		br.close();
		wordsfinal = list.toArray(new String[ list.size() ]);								//convert back to array

		LinkedHashSet<String> lwords =  new LinkedHashSet<String>(Arrays.asList(wordsfinal));//convert to remove duplicates
		wordsfinal = lwords.toArray(new String[ lwords.size() ]);							//convert back to array
		return wordsfinal;
	}



	public List<String> setStopwords() throws IOException {
		List<String> stopwords = new ArrayList<String>();
		File f = new File(pathStopwords.getFile());
		Scanner s = new Scanner(f);
		while (s.hasNext()){
			stopwords.add(s.next());
		}
		return stopwords;
	}

	public String[] removeStopwords(List<String> stops , String[] words){
		List<String> clean_words = new ArrayList<>();
		for (String w : words){
			if (!stops.contains(w)){
				clean_words.add(w);
			}
		}
		return clean_words.toArray(new String[0]);
	}

}
