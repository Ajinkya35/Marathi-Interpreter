public class ConditionNode implements ASTNode {
    private ASTNode leftOperand;
    private String operator;
    private ASTNode rightOperand;

    public ConditionNode(ASTNode leftOperand, String operator, ASTNode rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public ASTNode getLeftOperand() {
        return leftOperand;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRightOperand() {
        return rightOperand;
    }

    @Override
    public String toString() {
        return "Condition:\n  Variable Name: " + leftOperand + "\n  Operator: " + operator + "\n  Value: " + rightOperand;
    }
}
