import java.util.*;

public class MarathiParser {
    private List<Token> tokens;
    private int currentPosition;

    public MarathiParser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentPosition = 0;
    }

    private Token currentToken() {
        if (currentPosition >= tokens.size()) {
            return null;
        }
        return tokens.get(currentPosition);
    }

    private boolean match(String type) {
        Token token = currentToken();
        return token != null && token.getType().equals(type);
    }

    private Token consume(String expectedType) {
        if (currentPosition >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input. Expected: " + expectedType);
        }
        Token token = tokens.get(currentPosition);
        if (!token.getType().equals(expectedType)) {
            throw new RuntimeException("Expected " + expectedType + " but found " + token.getType() + ": " + token.getValue());
        }
        currentPosition++;
        return token;
    }

    public ASTNode parse() {
        return parseBlock();
    }

    private ASTNode parseBlock() {
        List<ASTNode> statements = new ArrayList<>();
        while (currentToken() != null && !match("ELSE") && !match("END_WHILE") && !match("RBRACE")) {
            statements.add(parseStatement());
        }
        return new BlockNode(statements);
    }

    private ASTNode parseStatement() {
        if (match("VAR_DECL")) {
            return parseVariableDeclaration();
        } else if (match("PRINT")) {
            return parsePrintStatement();
        } else if (match("IF")) {
            return parseIfStatement();
        } else if (match("WHILE")) {
            return parseWhileLoop();
        } else if (match("FOR")) {  
            return parseForLoop();
        } else if (match("RETURN")) {  // Add case for return statements
            return parseReturnStatement();
        } else if (match("IDENTIFIER")) {  
            return parseAssignment();
        } else if (match("FUNCTION_DECL")) {  
            return parseFunctionDeclaration();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken());
        }
    }

    private ASTNode parseAssignment() {
        String variableName = consume("IDENTIFIER").getValue();  
        Token operator = consume("OPERATOR");  // Expect '='
        if (!operator.getValue().equals("=")) {
            throw new RuntimeException("Expected '=' but found: " + operator.getValue());
        }
        ASTNode expression = parseExpression();  
        consume("SEMICOLON");  
        return new AssignmentNode(variableName, expression);  
    }

    private ASTNode parseVariableDeclaration() {
        consume("VAR_DECL");
        String variableName = consume("IDENTIFIER").getValue();
        Token operator = consume("OPERATOR");  // Expect '='
        if (!operator.getValue().equals("=")) {
            throw new RuntimeException("Expected '=' but found: " + operator.getValue());
        }
        ASTNode value = parseExpression();
        consume("SEMICOLON");
        return new VariableDeclarationNode(variableName, value);
    }

    private ASTNode parsePrintStatement() {
        consume("PRINT");
        consume("LPAREN");
        ASTNode expression = parseExpression(); 
        consume("RPAREN");
        consume("SEMICOLON");
        return new PrintStatementNode(expression);
    }

    private ASTNode parseExpression() {
        ASTNode left = parsePrimary();  

        // Handle binary operations
        while (match("OPERATOR") && (currentToken().getValue().equals("+") || currentToken().getValue().equals("-") || currentToken().getValue().equals("*") || currentToken().getValue().equals("/"))) {
            String operator = consume("OPERATOR").getValue();  
            ASTNode right = parsePrimary();  
            left = new BinaryOperationNode(left, operator, right);  
        }

        return left;
    }

    private ASTNode parsePrimary() {
        if (match("STRING")) {
            return new StringNode(consume("STRING").getValue());  // Handle string literals
        } else if (match("IDENTIFIER")) {
            String variableName = consume("IDENTIFIER").getValue();
            // Check if this is a function call
            if (match("LPAREN")) {  // If there is a '(', it is a function call
                return parseFunctionCall(variableName);
            }
            return new VariableNode(variableName);  // Otherwise, it's a variable
        } else if (match("NUMBER")) {
            return new NumberNode(consume("NUMBER").getValue());  // Handle numbers
        } else if (match("LPAREN")) {
            consume("LPAREN");  // Consume the '('
            ASTNode expression = parseExpression();  // Parse the inner expression
            consume("RPAREN");  // Consume the ')'
            return expression;  // Return the inner expression as the primary value
        }
    
        throw new RuntimeException("Unexpected token: " + currentToken());
    }

    private ASTNode parseIfStatement() {
        consume("IF");
        consume("LPAREN");
        ASTNode condition = parseCondition();
        consume("RPAREN");
        ASTNode thenBranch = parseBlock();
        ASTNode elseBranch = null;
        if (match("ELSE")) {
            consume("ELSE");
            elseBranch = parseBlock();
        }
        return new IfStatementNode(condition, thenBranch, elseBranch);
    }

    private ASTNode parseWhileLoop() {
        consume("WHILE");
        consume("LPAREN");
        ASTNode condition = parseCondition();
        consume("RPAREN");
        ASTNode body = parseBlock();
        consume("END_WHILE"); 
        return new WhileLoopNode(condition, body);
    }

    private ASTNode parseForLoop() {
        consume("FOR");  
        consume("LPAREN");  
    
        ASTNode initialization;
        if (match("VAR_DECL")) {
            initialization = parseVariableDeclaration();
        } else {
            throw new RuntimeException("Expected variable declaration in for loop initialization");
        }
        consume("SEMICOLON");  
    
        ASTNode condition = parseCondition();  
        consume("SEMICOLON");  
    
        ASTNode increment = parseAssignment();  
        consume("RPAREN");  
    
        ASTNode body = parseBlock();
        consume("END_WHILE");  
    
        return new ForLoopNode(initialization, condition, increment, body);  
    }

    // Modified this to handle both variables and numbers
    private ASTNode parseCondition() {
        ASTNode leftOperand;
        if (match("NUMBER")) {
            leftOperand = new NumberNode(consume("NUMBER").getValue());
        } else if (match("IDENTIFIER")) {
            leftOperand = new VariableNode(consume("IDENTIFIER").getValue());
        } else {
            throw new RuntimeException("Expected NUMBER or IDENTIFIER but found: " + currentToken().getValue());
        }

        String operator = consume("OPERATOR").getValue();

        ASTNode rightOperand;
        if (match("NUMBER")) {
            rightOperand = new NumberNode(consume("NUMBER").getValue());
        } else if (match("IDENTIFIER")) {
            rightOperand = new VariableNode(consume("IDENTIFIER").getValue());
        } else {
            throw new RuntimeException("Expected NUMBER or IDENTIFIER but found: " + currentToken().getValue());
        }

        return new ConditionNode(leftOperand, operator, rightOperand);
    }

    private ASTNode parseFunctionDeclaration() {
        consume("FUNCTION_DECL");  
        String functionName = consume("IDENTIFIER").getValue();
        consume("LPAREN");  
        List<String> parameters = new ArrayList<>();
        while (!match("RPAREN")) {
            parameters.add(consume("IDENTIFIER").getValue());
            if (match("COMMA")) {
                consume("COMMA");
            }
        }
        consume("RPAREN");  
        consume("LBRACE");  
        ASTNode body = parseBlock();
        consume("RBRACE");  
        return new FunctionDeclarationNode(functionName, parameters, body);
    }

    private ASTNode parseFunctionCall(String functionName) {
        consume("LPAREN");  // Consume the '('
        List<ASTNode> arguments = new ArrayList<>();
        while (!match("RPAREN")) {
            arguments.add(parseExpression());  // Parse arguments as expressions
            if (match("COMMA")) {
                consume("COMMA");  // Consume the ',' between arguments
            }
        }
        consume("RPAREN");  // Consume the ')'
        return new FunctionCallNode(functionName, arguments);  // Return a FunctionCallNode
    }

    private ASTNode parseReturnStatement() {
        consume("RETURN");  // Consume the 'parat' keyword
        ASTNode returnValue = parseExpression();  // Parse the expression to return
        consume("SEMICOLON");  // Expect ';' at the end of the return statement
        return new ReturnNode(returnValue);  // Create and return a ReturnNode
    }
}
