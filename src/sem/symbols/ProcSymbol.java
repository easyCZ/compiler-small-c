package sem.symbols;


import ast.Procedure;
import sem.Symbol;

public class ProcSymbol extends Symbol {

    public final Procedure procedure;

    public ProcSymbol(Procedure procedure) {
        super(procedure.name);
        this.procedure = procedure;
    }

    @Override
    public String toString() {
        return "ProcSymbol(" + name + ")";
    }

    @Override
    public boolean isProc() {
        return true;
    }
}
