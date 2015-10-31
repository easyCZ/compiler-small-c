package sem;

public abstract class Symbol {

	public final String name;
	
	public Symbol(String name) {
		this.name = name;
	}

    public boolean isVar() {
        return false;
    }

    public boolean isProc() {
        return false;
    }
}
