package sem;

import ast.*;
import ast.expressions.Var;
import org.junit.Before;
import org.junit.Test;
import sem.symbols.ProcSymbol;
import sem.symbols.VarSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class NameAnalysisVisitorTest {

    private static final Var foo = new Var("foo");
    private static final Var bar = new Var("bar");
    private static final Var zoo = new Var("zoo");

    private static final VarDecl intFoo = new VarDecl(Type.INT, foo);
    private static final VarDecl charBar = new VarDecl(Type.CHAR, bar);
    private static final VarDecl voidZoo = new VarDecl(Type.VOID, zoo);

    private static final List<VarDecl> EMPTY_VARDECLS = new ArrayList<>();
    private static final List<Stmt> EMPTY_STATEMENTS = new ArrayList<>();
    private static final Block EMPTY_BLOCK = new Block(EMPTY_VARDECLS, EMPTY_STATEMENTS);

    private static final Procedure fooProc = new Procedure(Type.INT, "foo", EMPTY_VARDECLS, EMPTY_BLOCK);

    private static final List<VarDecl> vardecls = Arrays.asList(intFoo, charBar, voidZoo);
    private static final Procedure MAIN = new Procedure(Type.VOID, "main", EMPTY_VARDECLS, EMPTY_BLOCK);


    private Scope scope;
    private NameAnalysisVisitor sut;


    @Before
    public void setUp() throws Exception {
        scope = new Scope();
        sut = new NameAnalysisVisitor(scope);
    }

    /* Program */
    @Test
    public void visitProgram_iteratesAllVardecls() {
        Program program = new Program(vardecls, new ArrayList<Procedure>(), MAIN);
        sut.visitProgram(program);

        assertEquals(3, scope.getSymbolTable().size());
    }

    /* VarDecl */
    @Test
    public void visitVarDecl_symbolAddedToScope() {
        sut.visitVarDecl(new VarDecl(Type.INT, new Var("i")));
        assertEquals(1, scope.getSymbolTable().size());
    }

    @Test
    public void visitVarDecl_errorWhenDuplicateDecl() {
        sut.visitVarDecl(new VarDecl(Type.INT, new Var("i")));
        sut.visitVarDecl(new VarDecl(Type.INT, new Var("i")));

        assertEquals(1, sut.getErrorCount());
    }

    /* Var */
    @Test
    public void visitVar_failsWithUndeclaredVariable() {
        sut.visitVar(foo);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitVar_failsWithDeclarationAsMethod() {
        scope.put(new ProcSymbol(fooProc));

        sut.visitVar(foo);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitVar_assignsOriginalVardecl() {
        scope.put(new VarSymbol(intFoo));

        sut.visitVar(foo);
        assertEquals(0, sut.getErrorCount());
        assertNotNull(foo.getVarDecl());
        assertEquals(intFoo, foo.getVarDecl());
    }

    @Test
    public void visitVar_traversesScopes() {
        Scope old = scope;
        scope = new Scope(old);
        old.put(new VarSymbol(intFoo));

        sut.visitVar(foo);
        assertEquals(0, sut.getErrorCount());
        assertNotNull(foo.getVarDecl());
        assertEquals(intFoo, foo.getVarDecl());
    }


}