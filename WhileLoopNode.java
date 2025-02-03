public class WhileLoopNode implements ASTNode {
    private ASTNode condition;
    private ASTNode body;

    public WhileLoopNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "WhileLoopNode{" + "condition=" + condition + ", body=" + body + '}';
    }
}
