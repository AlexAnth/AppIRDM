package appGUiPackage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Naive {

	protected static List<String> dictionary = new ArrayList<String>();		//arraylist of words
	protected static int spammsgs=0; //spam count;
	protected static int hammsgs=0; //ham count;
	protected static List<Integer> hamfreq = new ArrayList<Integer>();	//ham frequencies
	protected static List<Integer> spamfreq = new ArrayList<Integer>();	//spam frequencies

	public Naive() throws IOException {
		

		URL pathFoldTrain = Naive.class.getResource("/TrainData");
		URL pathFoldTest = Naive.class.getResource("/TestData");

		File folder = new File(pathFoldTrain.getPath());
		File[] listOfFiles = folder.listFiles(); 		//files in folder

		MailReader reader = new MailReader();

		System.out.println("Analyzing content of mails...\n");
		for (File file : listOfFiles) {
			if (file.isFile()) {
				String path= pathFoldTrain.getPath() +"/"+ file.getName();
				//System.out.println(path);
				String[] wordsfinal= reader.loadMail(path);		//get array of unique words from mail
				if(file.getName().contains("spmsg")) {
					spammsgs++;
				}else {
					hammsgs++;
				}
				for(int i=0;i<wordsfinal.length;i++) {
					if(wordsfinal[i].length()>8) {
						int a=exists(dictionary,wordsfinal[i]);
						if(file.getName().contains("spmsg")) {			//check if mail is spam
							if(a>=0) {									//check if word exists in lexicon-return position
								
								spamfreq.set(a, (spamfreq.get(a)+1));  //word exists in dictionary, so add to the frequency
								
							}else {										//doesnt exist-add in lex-increase frequency
								dictionary.add(wordsfinal[i]);
								spamfreq.add(1);
								hamfreq.add(0);
							}
						}else {											//mail is ham
							
							if(a>=0) {									//check if word exists in lexicon-return position
								
								hamfreq.set(a, (hamfreq.get(a)+1));  	//word exists in dictionary, so add to the frequency
								
							}else {										//doesnt exist-add in lex-increase frequency
								dictionary.add(wordsfinal[i]);
								hamfreq.add(1);
								spamfreq.add(0);
							}
						}
					}
					
				}
			}
		}
		
		
		System.out.println("Reading of training data complete.");
		System.out.println("Spam "+spammsgs+" Ham: "+hammsgs);
		System.out.println("\nStarting evaluation on test data...\n");
		
		//read testing-folder data-mails
		
		folder = new File(pathFoldTest.getFile());
		listOfFiles = folder.listFiles(); 		//files in folder
		int c=0;
		int cofsp=0,cofh=0,corsp=0,corh=0;										//holds number of correct classifications
		int counter = 0;
		
		for (File file : listOfFiles) {
			counter++;
			if (file.isFile()) {

				
				String[] wordsfinal= reader.loadMail(pathFoldTest.getPath()+"/"+file.getName());		//get array of unique words from mail
				
				double spamprob=(double) spammsgs/(spammsgs+hammsgs);
				double hamprob= (double) hammsgs/(spammsgs+hammsgs);
				
				for (int i=1;i<wordsfinal.length;i++) {								//compute spam probability  start from 1 - position 0 is "subject" 
																					//so as to not affect the probability - "subject" exists in every mail
					int a=exists(dictionary,wordsfinal[i]);
					if(a>=0) {
						spamprob*= (double) (spamfreq.get(a)+1)/ (spammsgs+2);		//with (Laplace normalization)

					}		
				}
				
				for (int i=1;i<wordsfinal.length;i++) {								//compute ham probability
					
					int a=exists(dictionary,wordsfinal[i]);
					if(a>=0) {
						hamprob*= (double) (hamfreq.get(a)+1)/ (hammsgs+2);			//with (Laplace normalization)
					
					}
				}
				
				//classify as spam-ham
				if(file.getName().contains("spmsg")) {					//mail was spam
					cofsp++;
					if(spamprob>hamprob) {
						corsp++;
					}
				}else {													//mail was ham
					cofh++;
					if(spamprob<hamprob) {
						corh++;
					}
				}
			}
		}
		System.out.println("Results of spam filter on "+counter+" emails :");
		System.out.println("Accuracy on spam mails : "+(new DecimalFormat("#.00").format((double)corsp/cofsp*100))+" % ");
		System.out.println("Accuracy on ham mails : "+(new DecimalFormat("#.00").format((double)corh/cofh*100))+" % ");


	}

	public String waitforUserInput(String mailpath) {
		String out = null;
		try {
			File email = new File(mailpath);
			MailReader reader = new MailReader();
			String[] wordsfinal = reader.loadMail(email.getAbsolutePath());

			double spamprob = (double) spammsgs / (spammsgs + hammsgs);
			double hamprob = (double) hammsgs / (spammsgs + hammsgs);

			for (int i = 1; i < wordsfinal.length; i++) {                                //compute spam probability  start from 1 - position 0 is "subject"
				//so as to not affect the probability - "subject" exists in every mail
				int a = exists(dictionary, wordsfinal[i]);
				if (a >= 0) {
					spamprob *= (double) (spamfreq.get(a) + 1) / (spammsgs + 2);        //with (Laplace normalization)

				}
			}

			for (int i = 1; i < wordsfinal.length; i++) {                                //compute ham probability

				int a = exists(dictionary, wordsfinal[i]);
				if (a >= 0) {
					hamprob *= (double) (hamfreq.get(a) + 1) / (hammsgs + 2);            //with (Laplace normalization)

				}
			}

			//classify as spam-ham
			if (spamprob > hamprob) {
				out = "Mail classified as spam.";
			} else {                                                    //mail was ham
				out = "Mail classified as legitimate.";
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return out;
	}

	public static int exists(List<String> lex,String w) {
		if(!lex.isEmpty()) { //has words in it
			for(int i=0;i<lex.size();i++) {
				if(w.equals(lex.get(i))) {
					return i;
				}
			}
			return -1;
		}else{
			return -1;
		}
	}

}
