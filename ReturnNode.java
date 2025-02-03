public class ReturnNode implements ASTNode {
    private ASTNode returnValue;

    public ReturnNode(ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    public ASTNode getReturnValue() {
        return returnValue;
    }
}
