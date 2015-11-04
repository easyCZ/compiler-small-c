package sem;

import ast.Type;
import ast.expressions.ChrLiteral;
import ast.expressions.IntLiteral;
import ast.expressions.StrLiteral;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TypeCheckVisitorTest {

    private TypeCheckVisitor sut;

    private static final IntLiteral ONE = new IntLiteral(1);
    private static final ChrLiteral A = new ChrLiteral('a');
    private static final StrLiteral WORD = new StrLiteral("word");

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

    private void assertNoErrors() {
        assertEquals(0, sut.getErrorCount());
    }

    @Test
    public void var_
}