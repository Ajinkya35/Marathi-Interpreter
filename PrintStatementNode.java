public class PrintStatementNode implements ASTNode {
    private ASTNode message;

    public PrintStatementNode(ASTNode message) {
        this.message = message;
    }

    public ASTNode getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PrintStatementNode{" + "message=" + message + '}';
    }
}
