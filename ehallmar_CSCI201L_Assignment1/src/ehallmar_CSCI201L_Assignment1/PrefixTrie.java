package ehallmar_CSCI201L_Assignment1;

import java.util.ArrayList;

// Prefix Trie to help with word lookup
// Supports insert and boolean checks for existence
//	as either a valid prefix or a valid word
//	and getPrefixWords, which returns all words that have
//	the input as a prefix
public class PrefixTrie {
	public PrefixTrie() {
		rootNode = new TrieNode(null,false,"");
	}
	private TrieNode rootNode;
	
	// Insert a string into the prefix trie
	public void insert(String s) { 
		s = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		TrieNode start = rootNode;
		for(int i = 0; i<s.length(); i++) {
			if(start.children[(int)s.charAt(i)-(int)'a']==null) {
				boolean endOfWord;
				if(i==s.length()-1) {
					endOfWord = true;
				} else {
					endOfWord = false;
				}
				start.children[(int)s.charAt(i)-(int)'a'] = new TrieNode(start,endOfWord,s);
			} else if (i==s.length()-1) {
				start.children[(int)s.charAt(i)-(int)'a'].isWord = true;
				start.children[(int)s.charAt(i)-(int)'a'].word = s;
			}
			start = start.children[(int)s.charAt(i)-(int)'a'];
		}
	}
	
	// Check if a string exists in the prefix trie
	public boolean isWord(String s) {
		s = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		TrieNode start = rootNode;
		for(int i = 0; i<s.length(); i++) {
			if(start.children[(int)s.charAt(i)-(int)'a']!=null) {
				if(i==s.length()-1) {
					return (start.children[(int)s.charAt(i)-(int)'a'].isWord);
				} else {
					start = start.children[(int)s.charAt(i)-(int)'a'];
				}
			} else { break; }
		}
		return false;
	}
	
	// Check if a string is a valid prefix even if not a word
	public boolean isPrefix(String s) {
		if(s=="" || s==null) {
			return true; // special case
		}
		s = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		TrieNode start = rootNode;
		for(int i = 0; i<s.length(); i++) {
			if(start.children[(int)s.charAt(i)-(int)'a']!=null) {
				if(i==s.length()-1) {
					return (true);
				} else {
					start = start.children[(int)s.charAt(i)-(int)'a'];
				}
			} else { break; }
		}
		return false;
	}
	// Returns an ArrayList of all the words that contain
	// 	the input as a prefix 
	// Limit's finding children after 10 are found
	//	since these won't be considered for now
	public ArrayList<String> getPrefixWords(String s) {
		s = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		ArrayList<String> words = new ArrayList<String>();
		TrieNode start = rootNode;
		ArrayList<TrieNode> nodes = new ArrayList<TrieNode>();
		for(int i = 0; i<s.length(); i++) {
			if(start.children[(int)s.charAt(i)-(int)'a']!=null) {
				start = start.children[(int)s.charAt(i)-(int)'a'];
			} else { return words; }
		}
		nodes.add(start);
		TrieNode current_node;	
		while(nodes.isEmpty() == false) {
			current_node = nodes.get(nodes.size()-1);
			nodes.remove(nodes.size()-1);
			for(int i = 0; i < 26; i++) {
				if(current_node.children[i]!=null) {
					nodes.add(current_node.children[i]);
					if(current_node.children[i].isWord) {
						words.add(current_node.children[i].word);
					}
				}
			}
		}
		return words;
	}
}

