package sem;

import ast.*;
import ast.expressions.FunCallExpr;
import ast.expressions.IntLiteral;
import ast.expressions.Var;
import ast.statements.Assign;
import ast.statements.FunCallStmt;
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

    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String ZOO = "zoo";

    private static final IntLiteral ONE = new IntLiteral(1);
    private static final IntLiteral TWO = new IntLiteral(2);

    private static final Var foo = new Var(FOO);
    private static final Var bar = new Var(BAR);
    private static final Var zoo = new Var(ZOO);

    private static final VarDecl intFoo = new VarDecl(Type.INT, foo);
    private static final VarDecl charBar = new VarDecl(Type.CHAR, bar);
    private static final VarDecl voidZoo = new VarDecl(Type.VOID, zoo);

    private static final List<VarDecl> EMPTY_VARDECLS = new ArrayList<>();
    private static final List<Stmt> EMPTY_STATEMENTS = new ArrayList<>();
    private static final List<Expr> EMPTY_EXPRESSIONS = new ArrayList<>();
    private static final Block EMPTY_BLOCK = new Block(EMPTY_VARDECLS, EMPTY_STATEMENTS);

    private static final Procedure fooProc = new Procedure(Type.INT, FOO, EMPTY_VARDECLS, EMPTY_BLOCK);
    private static final Procedure barProc = new Procedure(Type.CHAR, BAR, EMPTY_VARDECLS, EMPTY_BLOCK);

    private static final List<VarDecl> vardecls = Arrays.asList(intFoo, charBar, voidZoo);
    private static final Procedure MAIN = new Procedure(Type.VOID, "main", EMPTY_VARDECLS, EMPTY_BLOCK);

    private static final FunCallExpr fooExpr = new FunCallExpr(FOO, EMPTY_EXPRESSIONS);
    private static final FunCallExpr barExpr = new FunCallExpr(BAR, EMPTY_EXPRESSIONS);

    private static final List<Expr> arguments = new ArrayList<Expr>() {{
        add(bar);
        add(zoo);
    }};

    private static final FunCallExpr fooExprWithArgs = new FunCallExpr(FOO, arguments);
    private static final FunCallStmt fooStmtWithArgs = new FunCallStmt(FOO, arguments);

    private static final BinOp onePlusTwo = new BinOp(ONE, Op.ADD, TWO);
    private static final BinOp fooPlusBar = new BinOp(foo, Op.ADD, bar);
    private static final BinOp fooExprPlusBarExpr = new BinOp(fooExpr, Op.ADD, barExpr);


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

        assertEquals(intFoo, ((VarSymbol) scope.lookupCurrent(FOO)).varDecl);
        assertEquals(charBar, ((VarSymbol) scope.lookupCurrent(BAR)).varDecl);
        assertEquals(voidZoo, ((VarSymbol) scope.lookupCurrent(ZOO)).varDecl);
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

    /* Procedure */
    @Test
    public void visitProcedure_StoresPrcedureInScope() {
        sut.visitProcedure(fooProc);
        assertEquals(fooProc, ((ProcSymbol) scope.lookup(fooProc.name)).procedure);
        assertEquals(0, sut.getErrorCount());
    }

    @Test
    public void visitProcedure_FailsWithDuplicateProcedure() {
        scope.put(new ProcSymbol(fooProc));

        sut.visitProcedure(fooProc);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitProcedure_FailsWithATakenName() {
        scope.put(new VarSymbol(intFoo));

        sut.visitProcedure(fooProc);
        assertEquals(1, sut.getErrorCount());
    }

    // TODO: visitProcedure with contents


    /* Bin Op */
    @Test
    public void visitBinOp_visitsBothSidesForIntLiterals() {
        sut.visitBinOp(onePlusTwo);
        assertEquals(0, sut.getErrorCount());
    }

    @Test
    public void visitBinOp_functionCallVarsUndeclaredFail() {
        sut.visitBinOp(fooPlusBar);
        assertEquals(2, sut.getErrorCount());
    }

    @Test
    public void visitBinOp_functionCallVarsDeclared() {
        scope.put(new VarSymbol(intFoo));
        scope.put(new VarSymbol(charBar));

        sut.visitBinOp(fooPlusBar);
        assertEquals(0, sut.getErrorCount());
    }

    @Test
    public void visitBinOp_functionCallExpressionsUndeclared() {
        sut.visitBinOp(fooExprPlusBarExpr);
        assertEquals(2, sut.getErrorCount());
    }

    @Test
    public void visitBinOp_functionCallExpressionsDeclared() {
        scope.put(new ProcSymbol(fooProc));
        scope.put(new ProcSymbol(barProc));

        sut.visitBinOp(fooExprPlusBarExpr);
        assertEquals(0, sut.getErrorCount());
    }

    /* FunCallExpr */
    @Test
    public void visitFunCallExpr_failsWithUndefinedName() {
        sut.visitFunCallExpr(fooExpr);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitFunCallExpr_nameDefined() {
        scope.put(new ProcSymbol(fooProc));

        sut.visitFunCallExpr(fooExpr);
        assertEquals(0, sut.getErrorCount());
        assertEquals(fooExpr.getProcedure(), fooProc);
    }

    @Test
    public void visitFunCallExpr_failsWithUndefinedArgument() {
        scope.put(new ProcSymbol(fooProc));

        sut.visitFunCallExpr(fooExprWithArgs);
        assertEquals(2, sut.getErrorCount());
    }

    @Test
    public void visitFunCallExpr_WithArguments() {
        scope.put(new ProcSymbol(fooProc));
        scope.put(new VarSymbol(charBar));
        scope.put(new VarSymbol(voidZoo));

        sut.visitFunCallExpr(fooExprWithArgs);
        assertEquals(0, sut.getErrorCount());
        assertEquals(fooExprWithArgs.getProcedure(), fooProc);
    }

    /* FunCallStmt */
    @Test
    public void visitFunctionCallStmt_WithArgs() {
        scope.put(new ProcSymbol(fooProc));
        scope.put(new VarSymbol(charBar));
        scope.put(new VarSymbol(voidZoo));

        sut.visitFunctionCallStmt(fooStmtWithArgs);
        assertEquals(0, sut.getErrorCount());
        assertEquals(fooStmtWithArgs.getProcedure(), fooProc);

    }

    private static final Assign fooEqualsOne = new Assign(foo, ONE);
    private static final Assign barEqualsFooExpr = new Assign(bar, fooExpr);

    /* Assignment */
    @Test
    public void visitAssignment_failsWithLHSUndeclared() {
        sut.visitAssign(fooEqualsOne);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitAssignment_failsWithRHSUndeclared() {
        scope.put(new VarSymbol(charBar));
        sut.visitAssign(barEqualsFooExpr);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitAssignment_failsWithLHSDeclaredAsProcedure() {
        scope.put(new ProcSymbol(fooProc));
        sut.visitAssign(fooEqualsOne);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void visitAssignment_passesWithBothSidesDeclared() {
        scope.put(new VarSymbol(charBar));
        scope.put(new ProcSymbol(fooProc));
        sut.visitAssign(barEqualsFooExpr);
        assertEquals(0, sut.getErrorCount());
    }




}