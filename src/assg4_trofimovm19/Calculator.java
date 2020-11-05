package assg4_trofimovm19;

import java.util.EmptyStackException;
import java.util.Stack;

public class Calculator {
    private final String infixExpression;
    private String postfixExpression;
    private boolean isPrevOperand = false;

    public Calculator(String expression) {
        infixExpression = expression.replaceAll(",", ".").strip();
        convertPostfix();
    }

    private boolean convertPostfix() {
        Stack<Character> stack = new Stack<Character>();
        StringBuilder postfixExpression = new StringBuilder();

        char[] infixArray = infixExpression.toCharArray();

        for (var character: infixArray) {
            if (character == ' ' || character == '\t' || character == '\n'
                    || character == '\r' || character == '\0') {
                continue;
            }

            if (isOperand(character)) {
                appendInPostfix(postfixExpression, character);
            } else if (isOperator(character)) {
                while (!stack.empty() && !isOpeningParentheses(stack.lastElement())
                        && hasHigherPrecedence(stack.lastElement(), character)) {
                    appendInPostfix(postfixExpression, stack.pop());
                }

                stack.push(character);
                isPrevOperand = false;
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
        Stack<Double> stack = new Stack<Double>();
        StringBuilder string = new StringBuilder();
        var temporal = this.postfixExpression + ' ';
        char[] postfixExpression = temporal.toCharArray();

        for (int i = 0; i < postfixExpression.length; i++){
            string.delete(0, string.length());

            while(postfixExpression[i] != ' ') {
                string.append(postfixExpression[i]);
                i++;
            }

            if(isNumeric(string.toString())) {
                stack.push(Double.valueOf(string.toString()));
            } else {
                try {
                    stack.push(calculate(string.charAt(0), stack.pop(), stack.pop()));
                } catch (EmptyStackException e) {
                    exit("Problem with operators.");
                    System.exit(1);
                }
            }
        }

        return stack.pop();
    }

    private double calculate(Character operator, Double firstValue, Double secondValue){
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
        return character == '(' || character == '{' || character == '[';
    }

    private boolean isClosingParentheses(char character) {
        return character == ')' || character == '}' || character == ']';
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
        boolean isOperand = isOperand(character);
        if (isOperand != this.isPrevOperand) {
            stringBuilder.append(' ');
        }

        stringBuilder.append(character);
        this.isPrevOperand = isOperand;
    }

    private void exit(String errorMessage){
        System.err.println("ERROR -> " + errorMessage);
        System.exit(1);
    }

    public String getPostfix() {
        return postfixExpression.toString();
    }

    public String toString() {
        return infixExpression.toString();
    }
}
