package ehallmar_CSCI201L_Assignment1;

import java.util.Comparator;

// This comparator sorts on score (ascending)
public class CompareScore implements Comparator<SortHelper> {
	public int compare(SortHelper first, SortHelper second) {
        Integer First = first.score;
        Integer Second = second.score;
        return Integer.compare(First, Second);
    }
}
