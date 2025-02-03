public class AssignmentNode implements ASTNode {
    private String variableName;
    private ASTNode expression;

    public AssignmentNode(String variableName, ASTNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "AssignmentNode{" + "variableName='" + variableName + '\'' + ", expression=" + expression + '}';
    }
}
