public class VariableDeclarationNode implements ASTNode {
    private String variableName;
    private ASTNode value;  // Change from String to ASTNode

    public VariableDeclarationNode(String variableName, ASTNode value) {  // Update constructor
        this.variableName = variableName;
        this.value = value;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getValue() {  // Ensure value returns an ASTNode
        return value;
    }

    // You can add more methods as needed for this node type
}
