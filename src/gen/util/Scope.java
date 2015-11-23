package gen.util;


import java.util.HashMap;

public class Scope extends HashMap<String, Var> {

    private Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public Scope() {
        this.parent = null;
    }

    public Var search(String name) {
        if (this.containsKey(name)) return get(name);
        else if (parent != null) return parent.search(name);
        else return null;
    }

}
