package ehallmar_CSCI201L_Assignment1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;

public class Controller {
	// This method returns an ArrayList of words sorted by "score"
	private static ArrayList<String> sort(HashSet<String> words, String comp) {
		ArrayList<SortHelper> sortedWords = new ArrayList<SortHelper>();
		Integer current_score;
		for(String w: words) {
			current_score = 0;
			current_score += w.length()-comp.length();
			for(int i = 0; i < comp.length(); i++) {
				if (!(comp.charAt(i)==w.charAt(i))) {
					current_score += 1;
				}
			}
			sortedWords.add(new SortHelper(current_score,w));
		}
		// Sort
		CompareScore comparator = new CompareScore();
		sortedWords.sort(comparator);
		ArrayList<String> to_return = new ArrayList<String>();
		int last_score = 0;
		int current = 0;
		for(SortHelper get_string: sortedWords) {
			if (current < 10) {
				to_return.add(get_string.word);
				current++;
			} else if (current==10 && get_string.score == last_score) {
				to_return.add(get_string.word);
			} else { break; }
			last_score = get_string.score;
		}
		return to_return;
	}
	public static void main(String [] args){
		String word_file = null;
		String keyboard_file = null;
		String text_file = null;
		
		// Try to get args
		for(String s: args) {
			if(s.endsWith(".txt")) {
				text_file = s;
			} else if (s.endsWith(".wl")) {
				word_file = s;
			} else if (s.endsWith(".kb")) {
				keyboard_file = s;
			}
		}
		
		// Make sure we get all the input required
		Scanner scan = new Scanner(System.in);
		while (text_file == null || !text_file.endsWith(".txt")) {
			System.out.println("Please enter text file: ");
			text_file = scan.next().trim();			
		}
		while (word_file == null || !word_file.endsWith(".wl")) {
			System.out.println("Please enter word list file: ");
			word_file = scan.next().trim();
		}
		while (keyboard_file == null || !keyboard_file.endsWith(".kb")) {
			System.out.println("Please enter keyboard file: ");
			keyboard_file = scan.next().trim();
		}
		// Get words and setup Word Hash and Prefix Trie
		HashSet<String> allWords = new HashSet<String>();
		PrefixTrie prefixWords = new PrefixTrie();
		try {
			BufferedReader words_in = new BufferedReader(new FileReader(word_file));
		    String line = null;
		    while ((line = words_in.readLine()) != null) {
		    	for (String w: line.split("\\ ")) {
		    		w = w.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		    		if(w.length() > 0) {
		    			allWords.add(w);
		    			prefixWords.insert(w);
		    		}
		    	}
		    }
		    words_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Word list file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Word list file");
			System.exit(1);
		}
		
		// Next, we build our keyboard config Hash
		Hashtable<String,ArrayList<String>> keyboardHash = new Hashtable<String,ArrayList<String>>();
		try {
			BufferedReader keyboard_in = new BufferedReader(new FileReader(keyboard_file));
		    String line = null;
		    while ((line = keyboard_in.readLine()) != null) {
		    	ArrayList<String> neighbors = new ArrayList<String>();
		    	for (String w: line.split("\\,")[1].toLowerCase().split("")) {
		    		if(!w.equals(line.split("\\,"))) {
		    			// No self loops
			    		neighbors.add(w);
		    		}
		    	}			
		    	keyboardHash.put(line.split("\\,")[0], neighbors);
		    }
		    keyboard_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Keyboard file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Keyboard file");
			System.exit(1);
		}
		
		// Now, we can examine the text file
		Hashtable<String,HashSet<String>> corrections = new Hashtable<String,HashSet<String>>();
		ArrayList<String> ordered_errors = new ArrayList<String>();
		try {
			BufferedReader text_in = new BufferedReader(new FileReader(text_file));
		    String line = null;
		    while ((line = text_in.readLine()) != null) {
		    	for (String w: line.split("\\ ")) {
		    		w = w.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		    		if(w.length() > 0) {
		    			// w is now a formatted word
		    			if(!allWords.contains(w)) {
		    				// Not a correct word
			    			ArrayList<String> similar_words = new ArrayList<String>();
			    			Hashtable<String,HashSet<Integer>> already_tried = new Hashtable<String,HashSet<Integer>>();
			    			String current;
		    				ordered_errors.add(w);
		    				similar_words.add(w);
		    				//	Find corrections and completions
		    				HashSet<String> word_corrections = new HashSet<String>();
		    				corrections.put(w, word_corrections);
		    				HashSet<String> already_visited = new HashSet<String>();
		    				while(similar_words.isEmpty() == false) {
		    					current = similar_words.get(0);
		    					already_visited.add(current);
		    					if(!already_tried.containsKey(current)) {
		    						already_tried.put(current, new HashSet<Integer>());
		    					}
		    					similar_words.remove(0);
		    					if(allWords.contains(current)) {
		    						word_corrections.add(current);	
		    					}
    							// Pluck potential completed words out of prefix tree
		    					word_corrections.addAll(prefixWords.getPrefixWords(current));
		    					
		    					int position = current.length();
		    					HashSet<Integer> included_nums = already_tried.get(current);
		    					for(int i = 0; i < current.length(); i++) {
		    						if(current.charAt(i)==w.charAt(i) && !included_nums.contains(i)) {
		    							position = i;
		    							included_nums.add(i);
		    							break;
		    						}
		    					}
		    					if(position == current.length()) {
		    						continue;
		    					}
    							Character c = current.charAt(position);
		    					// position of first similar character to original string
    							for(String s: keyboardHash.get(c.toString())) {
    								String to_add;
    								if(current.length()==1) {
		    							to_add = s;
		    						} else {
		    							to_add = current.substring(0,position)+s+current.substring(position+1);
		    						}
    								if(!already_visited.contains(to_add) && prefixWords.isPrefix(to_add.substring(0,position+1))) {
    									similar_words.add(to_add);
    									already_visited.add(to_add);
    								}
    							}
								similar_words.add(current);
		    				} 
		    			}
		    		}
		    	}
		    }
		    text_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Text file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Text file");
			System.exit(1);
		}
		
		Hashtable<String,ArrayList<String>> clean_corrections = new Hashtable<String,ArrayList<String>>();
		// Rank the suggestions
		for(String to_sort: corrections.keySet()) {
			clean_corrections.put(to_sort, sort(corrections.get(to_sort),to_sort));
		}

		// Output results
    	try {
    		Format dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    		String fname = dateFormat.format(new Date()) + ".txt";
        	File file = new File(fname);
        	//boolean truth = file.createNewFile();
        	//System.out.println(truth);
        	Writer writer = new BufferedWriter(new FileWriter(file));
    		for(String word: ordered_errors) {
    			writer.write(word+" -");
    			for(String correction: clean_corrections.get(word)) {
    				writer.write(" "+correction);
    			}
    			writer.write("\n");
    		}
	    	writer.close();
		} catch (IOException e) {
			System.out.println("Error writing to file");
			System.exit(1);
		}
	 
	}
}
