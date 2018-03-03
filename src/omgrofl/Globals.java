package omgrofl;

import java.util.Scanner;

public class Globals {
    public static final String numberPattern = "^[0-9]+$";
    public static final String variablePattern = "^l[o]+l$";
    
    public static final String breakOperator = "tldr";
    public static final String endOperator = "brb";
    public static final String commentOperator = "w00t";
    public static final String loopOperator = "rtfm";
    public static final String forLoopOperator = "4";
    public static final String toOperator = "2";
    public static final String printCharacterOperator = "rofl";
    public static final String readCharacterOperator = "stfw";
    public static final String exitOperator = "stfu";
    public static final String sleepOperator = "afk";
    public static final String incrementVariableOperator = "lmao";
    public static final String decrementVariableOperator = "roflmao";
    public static final String conditionOperator = "wtf";
    
    public static final String stackPushOperator = "n00b";
    public static final String stackPopOperator = "l33t";
    public static final String dequeueOperator = "haxor";
    
    public static final String isOperator = "iz";
    public static final String negationOperator = "nope";
    public static final String equalsOperator = "liek";
    public static final String greaterOperator = "uber";
    
    public static final int minAllowedValue = 0;
    public static final int maxAllowedValue = 255;
    
    public static final String defaultEncoding = "ISO-8859-1";
    
    public static boolean validValue(int value) {
        return (value >= minAllowedValue)
                && (value <= maxAllowedValue);
    }
    
    public static int adjustValue(int value) {
        return value & maxAllowedValue;
    }
    
    public static String indent(String text) {
        Scanner scanner = new Scanner(text);
        StringBuilder stringBuilder = new StringBuilder();
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            stringBuilder.append("    ");
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        
        return stringBuilder.toString();
    }
}
