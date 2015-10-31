package sem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Scope outer;
    private Map<String, Symbol> symbolTable;

	
	public Scope(Scope outer) { 
		this.outer = outer;
        symbolTable = new HashMap<>();
	}
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		// TODO
        throw new NotImplementedException();
//		return null;
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
