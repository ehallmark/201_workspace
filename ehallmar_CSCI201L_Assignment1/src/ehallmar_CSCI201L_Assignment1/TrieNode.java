package ehallmar_CSCI201L_Assignment1;

// Quick and dirty trie node implementation
// Each node has a fixed size array of length 26 
// 	representing each lowercase letter in the alphabet
//	in order [a,b,c,...,z]
public class TrieNode {
	public TrieNode(TrieNode _parent, boolean _isWord, String _word) {
		parent = _parent;
		children = new TrieNode[26];
		isWord = _isWord;
		word = _word;
	}
	public boolean isWord;
	public TrieNode parent;
	public TrieNode[] children;
	public String word;
}
