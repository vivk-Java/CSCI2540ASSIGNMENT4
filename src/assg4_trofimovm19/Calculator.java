package assg4_trofimovm19;

import java.util.EmptyStackException;
import java.util.Stack;

public class Calculator {
    private final String infixExpression;
    private String postfixExpression;
    private boolean isPrevOperand = false;
    private boolean isPrevOperator = false;

    public Calculator(String expression) {
        infixExpression = expression.replaceAll(",", ".").strip();
        convertPostfix();
    }

    private boolean convertPostfix() {
        if (!infixVerify()) {
            return false;
        }

        Stack<Character> stack = new Stack<Character>();
        StringBuilder postfixExpression = new StringBuilder();

        char[] infixArray = infixExpression.toCharArray();

        for (char character : infixArray) {
            if (isWhitespace(character)) {
                continue;
            }

            if (isOperand(character)) {
                appendInPostfix(postfixExpression, character);
            } else if (isOperator(character)) {
                while (!stack.empty() && !isOpeningParentheses(stack.lastElement())
                        && hasHigherPrecedence(stack.lastElement(), character)) {
                    appendInPostfix(postfixExpression, stack.pop());
                }

                isPrevOperand = false;
                stack.push(character);
            } else if (isOpeningParentheses(character)) {
                stack.push(character);
            } else if (isClosingParentheses(character)) {
                while (!stack.empty() && !isOpeningParentheses(stack.lastElement())) {
                    appendInPostfix(postfixExpression, stack.pop());
                }

                stack.pop();
            }
        }

        while (!stack.empty()) {
            appendInPostfix(postfixExpression, stack.pop());
        }

        this.postfixExpression = postfixExpression.toString().strip();
        return true;
    }

    public double evaluate() {
        if (this.postfixExpression.isEmpty() || this.postfixExpression.isBlank()) {
            throw new IllegalStateException();
        }

        Stack<Double> stack = new Stack<Double>();
        StringBuilder string = new StringBuilder();
        var temporal = this.postfixExpression + ' ';
        char[] postfixArray = temporal.toCharArray();

        for (int i = 0; i < postfixArray.length; i++){
            string.delete(0, string.length());

            while(postfixArray[i] != ' ') {
                string.append(postfixArray[i]);
                i++;
            }

            if(isNumeric(string.toString())) {
                stack.push(Double.valueOf(string.toString()));
            } else {
                try {
                    stack.push(calculate(string.charAt(0), stack.pop(), stack.pop()));
                } catch (IllegalStateException e) {
                    exit("Problem with operators.");
                }
            }
        }

        return stack.pop();
    }

    private double calculate(Character operator, Double firstValue, Double secondValue) {
        if (operator == '+') {
            return secondValue + firstValue;
        } else if (operator == '-') {
            return secondValue - firstValue;
        } else if (operator == '*') {
            return secondValue * firstValue;
        } else if (operator == '/') {
            return secondValue / firstValue;
        }

        return 0;
    }

    private boolean infixVerify() {
        Stack<Character> stackCharacter = new Stack<Character>();
        Stack<Integer> stackIndex = new Stack<Integer>();

        boolean isPrevClosingParentheses = false;
        boolean isPrevOpeningParentheses = false;
        boolean isPrevWhitespace = false;
        boolean isPrevOperator = false;
        boolean isPrevOperand = false;
        boolean isFirst = true;


        char[] infixArray = infixExpression.toCharArray();
        for (int i = 0; i < infixArray.length; i++) {
            var character = infixArray[i];
            if (isWhitespace(character)) {
                isPrevWhitespace = true;
                continue;
            }

            var isClosingParentheses = isClosingParentheses(character);
            var isOpeningParentheses = isOpeningParentheses(character);
            var isOperator = isOperator(character);
            var isOperand = isOperand(character);

            if (isPrevOperator && isOperator) {
                exit(verifyError(this.infixExpression, "Second operator in row!", i));
                return false;
            }

            if (isOperator && isFirst) {
                exit(verifyError(this.infixExpression, "Operator before operand!", i));
                return false;
            }

            if (isPrevOperand && isOperand && isPrevWhitespace ||
                    isOperand && isPrevClosingParentheses) {
                exit(verifyError(this.infixExpression, "Second operand in row!", i));
                return false;
            }

            if (isOperator && isPrevOpeningParentheses) {
                 exit(verifyError(this.infixExpression, "Operator after opening bracket!", i));
                 return false;
            }

            if (isOpeningParentheses) {
                if (isPrevOperand) {
                    exit(verifyError(this.infixExpression, "Opening bracket after operand!", i));
                    return false;
                }

                stackCharacter.push(character);
                stackIndex.push(i);
            }

            if (isClosingParentheses) {
                if (isPrevOperator) {
                    exit(verifyError(this.infixExpression, "Operator before closing bracket!", i - 1));
                    return false;
                }

                if (isPrevOpeningParentheses) {
                    exit(verifyError(this.infixExpression, "Empty bracket!", i - 1));
                    return false;
                }

                if (stackCharacter.empty()) {
                    exit(verifyError(this.infixExpression, "Extra closing bracket!", i));
                    return false;
                }

                stackCharacter.pop();
                stackIndex.pop();
            }

            if (!isClosingParentheses && !isOpeningParentheses && !isOperator && !isOperand) {
                exit(verifyError(this.infixExpression, "Unknown character!", i));
                return false;
            }

            isPrevClosingParentheses = isClosingParentheses;
            isPrevOpeningParentheses = isOpeningParentheses;
            isPrevOperator = isOperator;
            isPrevOperand = isOperand;
            isPrevWhitespace = false;
            isFirst = false;
        }

        while (!stackCharacter.empty()) {
            exit(verifyError(this.infixExpression, "Extra opening bracket!", stackIndex.firstElement()));
            return false;
        }

        return true;
    }

    private String verifyError(String source, String error, Integer index) {
        Integer extraIndex = 9;

        String secondPart = " ".repeat(Math.max(0, index + extraIndex)) +
                "^";
        String thirdPart = " ".repeat(extraIndex) +
                error;
        return source + '\n' + secondPart + '\n' + thirdPart;
    }

    private boolean isWhitespace(Character character) {
        return character == ' ' || character == '\t' || character == '\n'
                || character == '\r' || character == '\0';
    }

    private boolean isNumeric(String number)  {
        try  {
            double d = Double.parseDouble(number);
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }


    private boolean isOperand(char character) {
        return character >= '0' && character <= '9'
                || character == '.';
    }

    private boolean isOperator(char character) {
        return character == '+' || character == '-' ||
                character == '*' || character == '/';
    }

    private boolean isOpeningParentheses(char character) {
        return character == '(';
    }

    private boolean isClosingParentheses(char character) {
        return character == ')';
    }

    private int getOperatorWeight(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }

        return -1;
    }

    private boolean hasHigherPrecedence(char first, char second) {
        int firstOperatorW = getOperatorWeight(first);
        int secondOperatorW = getOperatorWeight(second);

        if (firstOperatorW == secondOperatorW) {
            return true;
        }

        return firstOperatorW > secondOperatorW;
    }

    private void appendInPostfix(StringBuilder stringBuilder, char character) {
        boolean isOperator = isOperator(character);
        boolean isOperand = isOperand(character);
        if (isOperand != this.isPrevOperand || isOperator && this.isPrevOperator) {
            stringBuilder.append(' ');
        }

        stringBuilder.append(character);
        this.isPrevOperator = isOperator;
        this.isPrevOperand = isOperand;
    }

    private void exit(String errorMessage){
        System.err.println("ERROR -> " + errorMessage);
    }

    public String getPostfix() {
        if (this.postfixExpression == null ||this.postfixExpression.isEmpty()
                || this.postfixExpression.isBlank()) {
            throw new IllegalStateException();
        }

        return postfixExpression.toString();
    }

    public String toString() {
        return infixExpression.toString();
    }
}
