package edu.colgate.cosc290.itemsets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Colgate University COSC 290 Labs
 * Version 0.1,  2017
 *
 * @author Michael Hay
 * Edited by Caio Brighenti
 */
public class FrequentItemsetsMiner<E> {

    private final Transactions<E> transactions;
    private final int minSupport;
    private Set<Set<E>> F;  // the set of frequent item sets (those with support at least minSupport)
    private boolean resultsComputed; // flag indicating whether or not F has been computed

    /**
     * Constructor expects a set of transactions and a threshold between 0.0 and 1.0.  If n is the nunber of
     * transactions, this miner finds frequent itemsets that occur in at least (n * threshold) transactions.
     * @param ts a set of transactions
     * @param threshold minimum fraction of transactions an itemset must occur in to be considered frequent
     */
    public FrequentItemsetsMiner(Transactions<E> ts, double threshold) {
        if (threshold <= 0 || threshold > 1.0) {
            throw new RuntimeException("Expecting a threshold above 0.0 and at most 1.0 but got " + threshold);
        }
        transactions = ts;
        minSupport = (int) Math.ceil(threshold * ts.size());
        F = new HashSet<>();
    }

    /**
     * Clear previously computed results
     */
    public void reset() {
        resultsComputed = false;
        F.clear();
    }

    /**
     * Returns frequent itemsets of transactions
     * @return a set of sets, each of which is a frequent itemset
     */
    public Set<Set<E>> getFrequentItemsets() {
        execute();
        if (!resultsComputed) {
            throw new RuntimeException("Must first call execute method to generate results!");
        }
        return F;
    }


    /**
     * Computes frequent itemsets and saves results which can be obtained by calling getFrequentItemsets.  This
     * method is a correct but inefficient way of computing the frequent itemsets.
     */
    public void executeSLOW() {
        reset();

        Set<E> U = new HashSet<>();    // universe of all elements
        for (Set<E> t : transactions) {
            U.addAll(t);
        }

        for (Set<E> J : Utils.allSubsets(U)) {
            if (J.size() == 0) {  // don't count the empty set...
                continue;
            }
            // calculate the support of itemset J
            int support = 0;
            for (Set<E> t : transactions) {
                if (t.containsAll(J)) {
                    support++;
                }
            }
            // check: does J have enough support to be considered frequent?
            if (support >= minSupport) {
                F.add(J);
            }
        }
        resultsComputed = true;
    }

    /**
     * Computes frequent itemsets and saves results which can be obtained by calling getFrequentItemsets.  This
     * method should compute the same result as executeSLOW but in a more efficient manner.
     */
    public void execute() {
        // obtain frequent items
        Set<E> freq_items = frequentItems(minSupport);
        // add each to a singleton set
        Set<Set<E>> freq_sets = new HashSet<>();
        for (E el : freq_items) {
          Set<E> temp_set = new HashSet<>();
          temp_set.add(el);
          freq_sets.add(temp_set);
        }
        // generate candidates until no candidates are found
        while (freq_sets.size() > 0) {
          // add freq items of k-1 size to set of frequent itemsets
          F.addAll(freq_sets);
          Set<Set<E>> candidates = generateCandidates(freq_sets, freq_items);
          freq_sets.clear();
          // for each candidate size, only keep those with minimum minimum support
          for (Set<E> cand : candidates) {
            if (calcSupport(cand) >= minSupport)
              freq_sets.add(cand);
          }
        }
        resultsComputed = true;
    }

    /**
    * Calculates the support of a given itemset
    * @param itemset the itemset for which to calculate the support
    * @return an Integer support value
    */
    public Integer calcSupport(Set<E> itemset){
      int support = 0;
      for (Set<E> t : transactions) {
        if (t.containsAll(itemset))
          support++;
      }
      return support;
    }

    /**
     * Given a set of itemsets, currItemsets, all of size k-1, this generates a set of itemsets such that:
     * 1) each generated itemset S is of size k
     * 2) each generated itemset S consists of an itemset from currItemsets plus one additional item from itemsToAdd
     * 2) for each generated itemset S, every subset of S having size k-1 appears in currItemsets.
     * @param currItemsets a set of itemsets, all of the same size
     * @param itemsToAdd a set of items to add to each set in currItemSets
     * @return a set of itemsets
     */
    public Set<Set<E>> generateCandidates(Set<Set<E>> currItemSets, Set<E> itemsToAdd) {
      // initialize return variable
      Set<Set<E>> return_set = new HashSet<>();

      // iterate through sets and create all possible itemsets
      for (Set<E> s : currItemSets ) {
        for (E element : itemsToAdd) {
          if (!s.contains(element)){
            Set<E> temp_set = new HashSet<>(s);
            temp_set.add(element);
            return_set.add(temp_set);
          }
        }
      }

      // iterate through created itemsets and ensure validity
      Set<Set<E>> temp_set = new HashSet<>(return_set);
      for (Set<E> s : temp_set) {
        Set<Set<E>> subsets = Utils.allSubsetsOfSize(s, s.size() - 1);
        for (Set<E> ss : subsets) {
          if (!currItemSets.contains(ss))
            return_set.remove(s);
        }
      }

      return return_set;
    }

    /**
     * Computes the set of items that occur frequently
     * @param minSupport the minimum number of times an item must occur to be considered frequent
     * @return a set of items, which one occurring in at least threshold fraction of the transactions
     */
    public Set<E> frequentItems(int minSupport) {
      // initialize varaibles
      Set<E> return_set = new HashSet<>();
      Map<E, Integer> count_map = new HashMap<E, Integer>();

      // iterate through each element in each set and keep track of frequency in a hashmap
      for (Set<E> s : transactions) {
        for (E element : s ) {
          if (count_map.containsKey(element)){
            count_map.put(element, count_map.get(element) + 1);
            // if count matches support, add to return set
            if (count_map.get(element) == minSupport)
              return_set.add(element);
          }
          else
            count_map.put(element, 1);
        }
      }
      return return_set;
    }

    /**
     * A demonstration of how the various methods go together to compute frequent itemsets.
     */
    public static void demo() {
        List<Set<String>> transactionList = new LinkedList<>();
        transactionList.add(Utils.makeSet("bread", "milk"));
        transactionList.add(Utils.makeSet("bread", "diapers", "beer", "eggs"));
        transactionList.add(Utils.makeSet("milk", "diapers", "beer", "cola"));
        transactionList.add(Utils.makeSet("bread", "milk", "diapers", "beer"));
        transactionList.add(Utils.makeSet("bread", "milk", "diapers", "cola"));

        Transactions<String> transactions = new Transactions<>(transactionList);

        System.out.println("The dataset contains " + transactions.size() + " transactions: ");
        for (Set<String> t : transactions) {
            System.out.println(t);
        }
        System.out.println();

        double threshold = 0.6;
        System.out.println("Instantiating a frequent itemset miner with a support threshold of " + threshold);
        FrequentItemsetsMiner<String> fim = new FrequentItemsetsMiner<>(transactions, threshold);

        int minSupport = 3;
        System.out.println("The frequent items with min support of " + minSupport +
                " should be [bread, diapers, milk, beer]");
        Set<String> frequentItems = fim.frequentItems(minSupport);
        System.out.println("Your implementation produces these frequent items: " + frequentItems);
        System.out.println();


        System.out.println("The candidate 2-itemsets should be:\n" +
                "[bread, beer]\n" +
                "[diapers, milk]\n" +
                "[diapers, beer]\n" +
                "[milk, beer]\n" +
                "[bread, diapers]\n" +
                "[bread, milk]");
        System.out.println("Your implementation produces these candidate 2-itemsets:");
        // first, put each frequent item into a 1-itemset
        Set<Set<String>> F_1 = new HashSet<>();
        for (String item : frequentItems) {
            Set<String> oneItemset = new HashSet<>();
            oneItemset.add(item);
            F_1.add(oneItemset);
        }
        Set<Set<String>> C_2 = fim.generateCandidates(F_1, frequentItems);
        for (Set<String> itemset : C_2) {
            System.out.println(itemset);
        }
        System.out.println();

        System.out.println("Suppose that the frequent 2-itemsets are the following:");
        // suppose that frequent 2-itemsets are the following
        Set<Set<String>> F_2 = new HashSet<>();
        F_2.add(Utils.makeSet("beer", "diapers"));
        F_2.add(Utils.makeSet("bread", "diapers"));
        F_2.add(Utils.makeSet("bread", "milk"));
        F_2.add(Utils.makeSet("diapers", "milk"));
        for (Set<String> twoItemset : F_2) {
            System.out.println(twoItemset);
        }
        System.out.println("Given these frequent 2-itemsets, the candidate 3-itemsets should be:");
        System.out.println("[bread, diapers, milk]");
        System.out.println("Your implementation produces these candidate 3-itemsets:");
        Set<Set<String>> C_3 = fim.generateCandidates(F_2, frequentItems);
        for (Set<String> itemset : C_3) {
            System.out.println(itemset);
        }
        System.out.println();

        System.out.println("The set of all frequent itemsets should be: ");
        System.out.println("[[bread], [diapers], [diapers, milk], [diapers, beer], [milk], [bread, diapers], [beer], [bread, milk]]");
        System.out.println("Your implementation produces these frequent itemsets:");
        fim.execute();
        System.out.println(fim.getFrequentItemsets());
    }

    public static void main(String[] args) {
        demo();
    }

}
