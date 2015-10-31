package sem.symbols;


import ast.VarDecl;
import sem.Symbol;

public class VarSymbol extends Symbol {

    public final VarDecl varDecl;

    public VarSymbol(VarDecl varDecl) {
        super(varDecl.var.name);
        this.varDecl = varDecl;
    }

    @Override
    public String toString() {
        return "VarSymbol(" + name + ")";
    }
}
