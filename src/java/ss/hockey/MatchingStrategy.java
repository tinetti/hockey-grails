package ss.hockey;

/**
 * Given 2 collections A (full) and B (matching):
 * <ul>
 * <li>ANY: true iff A contains any items in B (A.intersect(B).size() > 0)</li>
 * <li>ALL: true iff A contains all items in B (A.containsAll(B))</li>
 * <li>ONLY: true iff every item in A is also in B (B.containsAll(A))</li>
 * <li>EXACT: true iff A and B are equal in size and contain the same items (A == B)</li>
 * </ul>
 * @author jtinett
 */
public enum MatchingStrategy {

    ANY, ALL, ONLY, EXACT

}
