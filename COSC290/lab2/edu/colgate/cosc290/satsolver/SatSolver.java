package edu.colgate.cosc290.satsolver;

import java.util.*;

/**
 * A SatSolver object is capable of checking whether or not a CNFF is satisfiable.
 *
 * Colgate University COSC 290 Labs
 * Version 0.1,  2017
 *
 * @author Michael Hay
 * edited by Caio Brighenti
 */
public class SatSolver {

    private final boolean findPureVariables; // if true, solver employs the pure variables strategy
    private final boolean findUnitClauses;  // if true, solver employs the variables in unit clauses strategy
    private int numRecursiveCalls = 0;


    /**
     * Construct a default CNFF sat solver.
     */
    public SatSolver() {
        this(false, false);
    }

    /**
     * Construct a CNFF sat solver.
     * @param findPureVariables if true, then solver will look for pure variables
     * @param findUnitClauses if true, then solver will look for variables in unit clauses
     */
    public SatSolver(boolean findPureVariables, boolean findUnitClauses) {
        this.findUnitClauses = findUnitClauses;
        this.findPureVariables = findPureVariables;
    }

    /**
     * Checks whether the CNFF is satisfiable.
     * @param CNFF the formula in conjunctive normal form
     * @return true if CNFF is satisfiable, false otherwise
     */
    public boolean isSatisfiable(CNFF CNFF) {
        Set<Variable> variables = new HashSet<>();
        variables.addAll(CNFF.getVariables());
        Model model = new Model(variables);
        numRecursiveCalls = 0;
        return isSatisfiableHelper(CNFF.getClauses(), model);
    }

    private boolean isSatisfiableHelper(Set<Clause> clauses, Model model) {
        numRecursiveCalls++;   // please leave this line
        // base cases, fail fast
        if (isTrueForModel(clauses, model))
          return true;
        if (isFalseForModel(clauses, model))
          return false;
        // recursive cases
        Variable var = model.chooseVar();
        model.assign(var, true);
        if (isSatisfiableHelper(clauses, model)){
          return true;
        }
        model.unassign(var);
        model.assign(var, false);
        if (isSatisfiableHelper(clauses, model)){
          return true;
        }
        model.unassign(var);
        return false;
    }

    // Checks if the formula is true for this model assignment
    private boolean isTrueForModel(Set<Clause> clauses, Model model) {
      for (Clause c : clauses) {
        if (!model.isTrue(c))
          return false;
      }
      return true;
    }

    // Checks if the frmula is false for this model assignment
    private boolean isFalseForModel(Set<Clause> clauses, Model model) {
      for (Clause c : clauses) {
        if (model.isFalse(c))
          return true;
      }
      return false;
    }

    /**
     * Returns the number of recursive calls made during execution of isSatisfiable.
     * @return number of recursive calls made in execution of isSatisfiable.
     */
    public int getSearchCost() {
        return numRecursiveCalls;
    }

    public static void main(String[] args) {
        SatSolver solver = new SatSolver();
        CNFF formula = CNFF.fromString("(p | q) & (~p | r)");
        boolean satisfiable = solver.isSatisfiable(formula);
        System.out.println("formula = " + formula + " " + (satisfiable? "is": "is not") + " satisfiable.");

        formula = CNFF.fromString("(p | q) & (~p | ~q) & (p | ~q) & (~p | q)");
        satisfiable = solver.isSatisfiable(formula);
        System.out.println("formula = " + formula + " " + (satisfiable? "is": "is not") + " satisfiable.");
    }

}
