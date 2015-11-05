package sem;

import ast.Program;
import sem.name.NameAnalysisVisitor;

import java.util.ArrayList;

public class SemanticAnalyzer {
	
	public int analyze(Program prog) {
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor());
//			add(new TypeCheckVisitor());
		}};
		// Error accumulator
		int errors = 0;
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		return errors;
	}
}
