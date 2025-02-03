import java.util.*;

public class MarathiTokenizer {
    private String input;
    private int position;

    public MarathiTokenizer(String input) {
        this.input = input;
        this.position = 0;
    }

    public Token nextToken() {
        skipWhitespace();

        if (position >= input.length()) {
            return null;
        }

        String currentSubstring = input.substring(position).toLowerCase();

        // Handle function declarations and calls
        if (currentSubstring.startsWith("karya")) {  // 'karya' for function declaration
            position += "karya".length();
            return new Token("FUNCTION_DECL", "karya");
        }
        if (currentSubstring.startsWith("parat")) {  // 'parat' for return statement
            position += "parat".length();
            return new Token("RETURN", "parat");
        }
        
        // Handle variable declaration and other keywords
        if (currentSubstring.startsWith("he aahe")) {
            position += "he aahe".length();
            return new Token("VAR_DECL", "He aahe");
        }
        if (currentSubstring.startsWith("jar")) {
            position += "jar".length();
            return new Token("IF", "Jar");
        }
        if (currentSubstring.startsWith("nahitar")) {
            position += "nahitar".length();
            return new Token("ELSE", "Nahitar");
        }
        if (currentSubstring.startsWith("chapa")) {
            position += "chapa".length();
            return new Token("PRINT", "Chapa");
        }
        if (currentSubstring.startsWith("joparyant")) {
            position += "joparyant".length();
            return new Token("WHILE", "joparyant");
        }
        if (currentSubstring.startsWith("sampel")) {
            position += "sampel".length();
            return new Token("END_WHILE", "sampel");
        }
        if (currentSubstring.startsWith("paryant")) {
            position += "paryant".length();
            return new Token("FOR", "paryant");
        }

        // Handling string literals
        if (input.charAt(position) == '"') {
            return readString();
        }

        char currentChar = input.charAt(position);

        if (Character.isDigit(currentChar)) {
            return readNumber();
        }
        if (Character.isLetter(currentChar)) {
            return readIdentifier();
        }

        // Handle comments starting with "//"
        if (input.startsWith("//", position)) {
            // Skip characters until the end of the line or input
            position += 2;  // Move past the "//"
            while (position < input.length() && input.charAt(position) != '\n') {
                position++;  // Skip everything on the line after "//"
            }
            return nextToken();  // Continue tokenizing after the comment
        }

        // Handling multi-character operators (==, !=)
        if (currentSubstring.startsWith("==")) {
            position += 2;
            return new Token("OPERATOR", "==");
        }
        if (currentSubstring.startsWith("!=")) {
            position += 2;
            return new Token("OPERATOR", "!=");
        }
        if (currentSubstring.startsWith("<=")) {
            position += 2;
            return new Token("OPERATOR", "<=");
        }
        if (currentSubstring.startsWith(">=")) {
            position += 2;
            return new Token("OPERATOR", ">=");
        }
        if (currentChar == '<') {
            position++;
            return new Token("OPERATOR", "<");
        }
        if (currentChar == '>') {
            position++;
            return new Token("OPERATOR", ">");
        }
        
        // Handling single-character operators
        if (currentChar == '=' || currentChar == '+' || currentChar == '-' ||
            currentChar == '*' || currentChar == '/' || currentChar == '%') {
            position++;
            return new Token("OPERATOR", String.valueOf(currentChar));
        }

        // Handling parentheses and other symbols
        if (currentChar == '(') {
            position++;
            return new Token("LPAREN", "(");
        }
        if (currentChar == ')') {
            position++;
            return new Token("RPAREN", ")");
        }
        if (currentChar == ';') {
            position++;
            return new Token("SEMICOLON", ";");
        }
        if (currentChar == '{') {
            position++;
            return new Token("LBRACE", "{");
        }
        if (currentChar == '}') {
            position++;
            return new Token("RBRACE", "}");
        }
        if (currentChar == ',') {
            position++;
            return new Token("COMMA", ",");
        }

        throw new RuntimeException("Unexpected character: " + currentChar);
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private Token readNumber() {
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        String number = input.substring(start, position);
        return new Token("NUMBER", number);
    }

    private Token readIdentifier() {
        int start = position;
        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))) {
            position++;
        }
        String identifier = input.substring(start, position);
        return new Token("IDENTIFIER", identifier);
    }

    private Token readString() {
        int start = position;
        position++; // Skip the opening quote
        while (position < input.length() && input.charAt(position) != '"') {
            position++;
        }
        if (position >= input.length()) {
            throw new RuntimeException("Unterminated string literal");
        }
        position++; // Skip the closing quote
        String stringLiteral = input.substring(start + 1, position - 1);
        return new Token("STRING", stringLiteral);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = nextToken()) != null) {
            System.out.println("Token: " + token);  // Debug output
            tokens.add(token);
        }
        return tokens;
    }
}
