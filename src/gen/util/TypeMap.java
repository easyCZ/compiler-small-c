package gen.util;


import java.util.HashMap;

public final class TypeMap extends HashMap<ast.Type, String> {

    public TypeMap() {
        put(ast.Type.STRING, "String");
        put(ast.Type.CHAR, ast.Type.CHAR.toString().toLowerCase());
        put(ast.Type.INT, ast.Type.INT.toString().toLowerCase());
        put(ast.Type.VOID, ast.Type.VOID.toString().toLowerCase());
    }

}
