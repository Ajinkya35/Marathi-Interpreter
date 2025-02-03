public class ForLoopNode implements ASTNode {
    private ASTNode initialization;
    private ASTNode condition;
    private ASTNode increment;
    private ASTNode body;

    public ForLoopNode(ASTNode initialization, ASTNode condition, ASTNode increment, ASTNode body) {
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    public ASTNode getInitialization() {
        return initialization;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getIncrement() {
        return increment;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ForLoopNode{" +
                "initialization=" + initialization +
                ", condition=" + condition +
                ", increment=" + increment +
                ", body=" + body +
                '}';
    }
}
