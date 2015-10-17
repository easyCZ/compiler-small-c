package parser.wrappers;


import ast.Type;
import ast.Var;

public class TypeIdentifier {

    public final Type type;
    public final Var var;

    public TypeIdentifier(Type type, Var var) {
        this.type = type;
        this.var = var;
    }

}
