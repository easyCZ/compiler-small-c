package sem.name.symbols;


import ast.VarDecl;
import sem.name.Symbol;

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

    @Override
    public boolean isVar() {
        return true;
    }

}
