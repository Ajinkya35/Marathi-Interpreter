import java.util.HashMap;
import java.util.List;

public class MarathiInterpreter {
    private HashMap<String, Integer> variableStore = new HashMap<>();  // Variable store
    private HashMap<String, FunctionDeclarationNode> functionStore = new HashMap<>();
    private StringBuilder outputBuffer = new StringBuilder();  // To store output

    // Getter for the output buffer
    public String getOutput() {
        return outputBuffer.toString();
    }

    public void interpret(ASTNode node) {
        if (node instanceof BlockNode) {
            interpretBlockNode((BlockNode) node);
        } else if (node instanceof IfStatementNode) {
            interpretIfStatementNode((IfStatementNode) node);
        } else if (node instanceof VariableDeclarationNode) {
            interpretVariableDeclarationNode((VariableDeclarationNode) node);
        } else if (node instanceof PrintStatementNode) {
            interpretPrintStatementNode((PrintStatementNode) node);
        } else if (node instanceof AssignmentNode) {
            interpretAssignmentNode((AssignmentNode) node);
        } else if (node instanceof WhileLoopNode) {
            interpretWhileLoopNode((WhileLoopNode) node);
        } else if (node instanceof ForLoopNode) {
            interpretForLoopNode((ForLoopNode) node);
        } else if (node instanceof FunctionDeclarationNode) {
            interpretFunctionDeclarationNode((FunctionDeclarationNode) node);
        } else if (node instanceof FunctionCallNode) {
            evaluateFunctionCall((FunctionCallNode) node);
        } else if (node instanceof ReturnNode) {
            // Handle return by evaluating the return value
            throw new ReturnException(evaluateExpression(((ReturnNode) node).getReturnValue()));
        } else {
            throw new RuntimeException("Unexpected AST node type: " + node.getClass().getName());
        }
    }

    private void interpretBlockNode(BlockNode blockNode) {
        for (ASTNode statement : blockNode.getStatements()) {
            interpret(statement);
        }
    }

    private void interpretIfStatementNode(IfStatementNode ifStmtNode) {
        ConditionNode condition = (ConditionNode) ifStmtNode.getCondition();
        if (evaluateCondition(condition)) {
            interpret(ifStmtNode.getThenBranch());
        } else if (ifStmtNode.getElseBranch() != null) {
            interpret(ifStmtNode.getElseBranch());
        }
    }

    private void interpretVariableDeclarationNode(VariableDeclarationNode varDeclNode) {
        String variableName = varDeclNode.getVariableName();
        ASTNode valueNode = varDeclNode.getValue();
        Object evaluatedValue = evaluateExpression(valueNode);

        if (evaluatedValue instanceof Integer) {
            variableStore.put(variableName, (Integer) evaluatedValue);
        } else {
            throw new RuntimeException("Variable '" + variableName + "' must be assigned an integer value.");
        }
    }

    private void interpretAssignmentNode(AssignmentNode assignmentNode) {
        String variableName = assignmentNode.getVariableName();
        Object value = evaluateExpression(assignmentNode.getExpression());

        if (value instanceof Integer) {
            variableStore.put(variableName, (Integer) value);
        } else {
            throw new RuntimeException("Unsupported value type for assignment: " + value.getClass().getName());
        }
    }

    private void interpretPrintStatementNode(PrintStatementNode printStmtNode) {
        String message = evaluateExpression(printStmtNode.getMessage()).toString();
        outputBuffer.append(message).append("\n");  // Capture output
    }

    private Object evaluateExpression(ASTNode node) {
        if (node instanceof BinaryOperationNode) {
            BinaryOperationNode binOp = (BinaryOperationNode) node;
            Object leftValue = evaluateExpression(binOp.getLeft());
            Object rightValue = evaluateExpression(binOp.getRight());

            switch (binOp.getOperator()) {
                case "+":
                    if (leftValue instanceof String || rightValue instanceof String) {
                        return leftValue.toString() + rightValue.toString();  // String concatenation
                    } else {
                        return Integer.parseInt(leftValue.toString()) + Integer.parseInt(rightValue.toString());  // Numeric addition
                    }
                case "-":
                    return Integer.parseInt(leftValue.toString()) - Integer.parseInt(rightValue.toString());  // Numeric subtraction
                case "*":
                    return Integer.parseInt(leftValue.toString()) * Integer.parseInt(rightValue.toString());  // Numeric multiplication
                case "/":
                    return Integer.parseInt(leftValue.toString()) / Integer.parseInt(rightValue.toString());  // Numeric division
                case "%":
                    return Integer.parseInt(leftValue.toString()) % Integer.parseInt(rightValue.toString());  // Modulo operation
                default:
                    throw new RuntimeException("Unknown operator: " + binOp.getOperator());
            }
        } else if (node instanceof StringNode) {
            return ((StringNode) node).getValue();
        } else if (node instanceof NumberNode) {
            return Integer.parseInt(((NumberNode) node).getValue());
        } else if (node instanceof VariableNode) {
            String variableName = ((VariableNode) node).getName();
            Object value = variableStore.get(variableName);
            if (value == null) {
                throw new RuntimeException("Undefined variable: " + variableName);
            }
            return value;
        } else if (node instanceof FunctionCallNode) {
            return evaluateFunctionCall((FunctionCallNode) node);
        }

        throw new RuntimeException("Unknown expression node: " + node);
    }

    private Object evaluateFunctionCall(FunctionCallNode functionCallNode) {
        String functionName = functionCallNode.getFunctionName();
        FunctionDeclarationNode functionNode = functionStore.get(functionName);
        if (functionNode == null) {
            throw new RuntimeException("Function not defined: " + functionName);
        }

        List<String> parameters = functionNode.getParameters();
        List<ASTNode> arguments = functionCallNode.getArguments();

        if (parameters.size() != arguments.size()) {
            throw new RuntimeException("Argument count mismatch for function: " + functionName);
        }

        HashMap<String, Integer> previousVariableStore = new HashMap<>(variableStore);

        for (int i = 0; i < parameters.size(); i++) {
            Object argumentValue = evaluateExpression(arguments.get(i));
            variableStore.put(parameters.get(i), (Integer) argumentValue);
        }

        Object returnValue = null;
        try {
            interpret(functionNode.getBody());
        } catch (ReturnException returnEx) {
            returnValue = returnEx.getValue();  // Capture the return value
        }

        variableStore = previousVariableStore;

        return returnValue;
    }

    private void interpretWhileLoopNode(WhileLoopNode whileLoopNode) {
        while (evaluateCondition((ConditionNode) whileLoopNode.getCondition())) {
            interpret(whileLoopNode.getBody());
        }
    }

    private boolean evaluateCondition(ConditionNode conditionNode) {
        Object leftValue = evaluateExpression(conditionNode.getLeftOperand());
        Object rightValue = evaluateExpression(conditionNode.getRightOperand());

        if (!(leftValue instanceof Integer) || !(rightValue instanceof Integer)) {
            throw new RuntimeException("Both operands must be integers for condition evaluation.");
        }

        String operator = conditionNode.getOperator();
        int leftIntValue = (Integer) leftValue;
        int rightIntValue = (Integer) rightValue;

        switch (operator) {
            case "<": return leftIntValue < rightIntValue;
            case ">": return leftIntValue > rightIntValue;
            case "<=": return leftIntValue <= rightIntValue;
            case ">=": return leftIntValue >= rightIntValue;
            case "==": return leftIntValue == rightIntValue;
            default: throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    private void interpretForLoopNode(ForLoopNode forLoopNode) {
        interpret(forLoopNode.getInitialization());

        while (evaluateCondition((ConditionNode) forLoopNode.getCondition())) {
            interpret(forLoopNode.getBody());
            interpret(forLoopNode.getIncrement());
        }
    }

    private void interpretFunctionDeclarationNode(FunctionDeclarationNode functionNode) {
        functionStore.put(functionNode.getFunctionName(), functionNode);
    }

    // Custom exception to handle return values
    public class ReturnException extends RuntimeException {
        private final Object value;

        public ReturnException(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}
