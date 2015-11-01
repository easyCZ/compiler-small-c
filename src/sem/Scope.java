package sem;

import ast.Procedure;
import sem.symbols.ProcSymbol;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Scope outer;
    private Map<String, Symbol> symbolTable;

	
	public Scope(Scope outer) { 
		this.outer = outer;
        symbolTable = new HashMap<>();

        // top level
        if (outer == null) {
            initBuiltIns();
        }
	}

    private void initBuiltIns() {
        symbolTable.put("print_i", new ProcSymbol(Procedure.PRINT_I));
        symbolTable.put("print_s", new ProcSymbol(Procedure.PRINT_C));
        symbolTable.put("print_c", new ProcSymbol(Procedure.PRINT_S));
        symbolTable.put("read_i", new ProcSymbol(Procedure.READ_I));
        symbolTable.put("read_c", new ProcSymbol(Procedure.READ_C));
    }
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		Symbol current = lookupCurrent(name);

        if (current == null && outer != null) {
            return outer.lookup(name);
        }

        return current;
	}
	
	public Symbol lookupCurrent(String name) {
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}

    public Map<String, Symbol> getSymbolTable() {
        return symbolTable;
    }

    public Scope getOuter() {
        return outer;
    }

    @Override
    public String toString() {
        return "Scope(" + outer + ", " + symbolTable +  ")";
    }
}
