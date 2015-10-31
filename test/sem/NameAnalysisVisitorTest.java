package sem;

import ast.*;
import ast.expressions.Var;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class NameAnalysisVisitorTest {

    private static final VarDecl intFoo = new VarDecl(Type.INT, new Var("foo"));
    private static final VarDecl charBar = new VarDecl(Type.CHAR, new Var("bar"));
    private static final VarDecl voidZoo = new VarDecl(Type.VOID, new Var("zoo"));

    private static final List<VarDecl> vardecls = Arrays.asList(intFoo, charBar, voidZoo);
    private static final Procedure MAIN = new Procedure(
            Type.VOID,
            "main",
            new ArrayList<VarDecl>(),
            new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())
    );

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


}