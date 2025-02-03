import java.util.List;  // Import the List class

public class FunctionDeclarationNode implements ASTNode {
    private String functionName;
    private List<String> parameters;
    private ASTNode body;

    public FunctionDeclarationNode(String functionName, List<String> parameters, ASTNode body) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public ASTNode getBody() {
        return body;
    }
}
