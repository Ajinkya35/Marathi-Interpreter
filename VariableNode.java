public class VariableNode implements ASTNode {
    private String name;

    public VariableNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VariableNode{" + "name='" + name + '\'' + '}';
    }
}
