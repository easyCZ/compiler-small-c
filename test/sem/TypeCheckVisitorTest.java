package sem;

import ast.Type;
import ast.VarDecl;
import ast.expressions.ChrLiteral;
import ast.expressions.IntLiteral;
import ast.expressions.StrLiteral;
import ast.expressions.Var;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TypeCheckVisitorTest {

    private TypeCheckVisitor sut;

    private static IntLiteral ONE = new IntLiteral(1);
    private static ChrLiteral A = new ChrLiteral('a');
    private static StrLiteral WORD = new StrLiteral("word");
    private static Var X = new Var("x");
    private static VarDecl X_DECL = new VarDecl(Type.INT, X);

    @Before
    public void setUp() throws Exception {
        sut = new TypeCheckVisitor();
    }

    /* Int Literal */
    @Test
    public void intLiteral() {
        sut.visitIntLiteral(ONE);
        assertNoErrors();
    }

    @Test
    public void intLiteral_returnsIntType() {
        Type type = sut.visitIntLiteral(ONE);
        assertEquals(Type.INT, type);
        assertNoErrors();
    }

    /* Chr literal */
    @Test
    public void charLiteral_returnsType() {
        Type type = sut.visitChrLiteral(A);
        assertEquals(Type.CHAR, type);
        assertNoErrors();
    }

    /* Str Literal */
    @Test
    public void strLiteral_returnsType() {
        Type type = sut.visitStrLiteral(WORD);
        assertEquals(Type.STRING, type);
        assertNoErrors();
    }

    @Test
    public void var_LooksUpTypeInEnv() {
        X.setVarDecl(X_DECL);
        Type type = sut.visitVar(X);
        assertEquals(X_DECL.type, type);
        assertNoErrors();
    }

    private void assertNoErrors() {
        assertEquals(0, sut.getErrorCount());
    }


}