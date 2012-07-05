package omgrofl.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import omgrofl.interpreter.commands.*;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.factories.ParameterFactory;
import omgrofl.interpreter.procedures.DecrementVariableProcedure;
import omgrofl.interpreter.procedures.IncrementVariableProcedure;
import omgrofl.interpreter.procedures.PrintCharacterProcedure;

public class ScriptParser {
    
    private Variable readVariable(Memory memory, Scanner scanner) throws ScriptParseException {
        try {
            String variableName = scanner.next();
            if (!variableName.toLowerCase().matches(Globals.variablePattern)) {
                throw new ScriptParseException(variableName
                        + " is not a valid variable name");
            }

            Variable variable = new Variable(memory, variableName);
            return variable;
            
        } catch (NoSuchElementException e) {
            throw new ScriptParseException("Missing variable");
        }
    }
    
    private Parameter getParameter(ParameterFactory parameterFactory, Memory memory,
            String name) throws ScriptParseException {
        if (name.toLowerCase().matches(Globals.variablePattern)) {
            Variable variable = new Variable(memory, name);
            return parameterFactory.getParameter(variable);
        } else if (name.matches(Globals.numberPattern)) {
            try {
                Integer integerValue = Integer.parseInt(name);
                return parameterFactory.getParameter(integerValue);
            } catch (NumberFormatException e) {
                throw new ScriptParseException("Wrong number format: " + name);
            }
        } else
            throw new ScriptParseException("Invalid parameter: " + name);
    }
    
    public String readNext(Scanner scanner) throws ScriptParseException {
        try {
            return scanner.next();
        } catch (NoSuchElementException e) {
            throw new ScriptParseException("Missing operand");
        }
    }
    
    private void expect(String value, String expected) throws ScriptParseException {
        if (!value.equalsIgnoreCase(expected))
            throw new ScriptParseException(expected + " expected, "
                    + value + " found");
    }
    
    public Script parse(File input, Memory memory) throws FileNotFoundException, ScriptParseException {
        Scanner scanner = new Scanner(input);
        CommandSequence commandSequence = parseSequence(scanner, memory);
        return new Script(commandSequence);
    }
    
    public Script parse(String input, Memory memory) throws ScriptParseException {
        Scanner scanner = new Scanner(input);
        CommandSequence commandSequence = parseSequence(scanner, memory);
        return new Script(commandSequence);
    }
    
    private CommandSequence parseSequence(Scanner scanner, Memory memory)
            throws ScriptParseException {
        CommandSequence script = new CommandSequence();
        ParameterFactory parameterFactory = new ParameterFactory();
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            
            // Empty line, skip
            if (!lineScanner.hasNext())
                continue;
            
            String commandName = lineScanner.next();
            
            if (commandName.equalsIgnoreCase(Globals.endOperator))
                // end of script
                break;
            
            else if (commandName.equalsIgnoreCase(Globals.commentOperator))
                // comment
                continue;
            
            else if (commandName.equalsIgnoreCase(Globals.loopOperator)) {
                // infinite loop (until broken)
                CommandSequence loopCommandSequence = parseSequence(scanner, memory);
                InfiniteLoopCommand loop = new InfiniteLoopCommand(loopCommandSequence);
                script.addCommand(loop);
                
            } else if (commandName.toLowerCase().matches(Globals.variablePattern)) {
                String varName = commandName;
                
                Variable variable = new Variable(memory, varName);
                Parameter valueParameter;
                
                try {
                    String operator = lineScanner.next();
                    String value = lineScanner.next();
                    Integer newValue;
                    
                    if (operator.equalsIgnoreCase(Globals.isOperator)) {
                        if (value.toLowerCase().matches(Globals.numberPattern)) {
                            newValue = Integer.parseInt(value);
                            if (!Globals.validValue(newValue))
                                throw new ScriptParseException("Unacceptable value: " + newValue);
                            valueParameter = parameterFactory.getParameter(newValue);
                        } else if (value.toLowerCase().matches(Globals.variablePattern)) {
                            Variable paramVariable = new Variable(memory, value);
                            valueParameter = parameterFactory.getParameter(paramVariable);
                        } else {
                            throw new ScriptParseException("Unrecognized operand " + value);
                        }
                    } else if (operator.equalsIgnoreCase("to") && value.equalsIgnoreCase("/dev/null")) {
                        newValue = 0;
                        valueParameter = parameterFactory.getParameter(newValue);
                    } else {
                        throw new ScriptParseException("Unrecognized operation with variable " + varName);
                    }
                    
                } catch (NoSuchElementException e) {
                    throw new ScriptParseException("Missing operands");
                }
                
                AssignmentCommand assignmentCommand = new AssignmentCommand(variable, valueParameter);
                script.addCommand(assignmentCommand);
                
            } else if (commandName.equalsIgnoreCase(Globals.breakOperator)) {
                BreakCommand breakCommand = new BreakCommand();
                script.addCommand(breakCommand);
                
            } else if (commandName.equalsIgnoreCase(Globals.printCharacterOperator)) {
                
                try {
                    String variableName = lineScanner.next();
                    
                    if (!variableName.toLowerCase().matches(Globals.variablePattern))
                        throw new ScriptParseException(variableName
                                + " is not a valid variable name");
                    
                    Variable variable = new Variable(memory, variableName);
                    Parameter variableParameter = parameterFactory.getParameter(variable);
                    
                    PrintCharacterProcedure printCharacterProcedure = new PrintCharacterProcedure();
                    printCharacterProcedure.addParameter(variableParameter);
                    
                    CallProcedureCommand command = new CallProcedureCommand(printCharacterProcedure);
                    
                    script.addCommand(command);
                } catch (NoSuchElementException e) {
                    throw new ScriptParseException("Missing operands");
                }
                
            } else if (commandName.equalsIgnoreCase(Globals.exitOperator)) {
                ExitCommand exitCommand = new ExitCommand();
                script.addCommand(exitCommand);
            } else if (commandName.equalsIgnoreCase(Globals.incrementVariableOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                IncrementVariableProcedure procedure = new IncrementVariableProcedure();
                procedure.addParameter(parameterFactory.getParameter(variable));
                CallProcedureCommand command = new CallProcedureCommand(procedure);

                script.addCommand(command);
                
            } else if (commandName.equalsIgnoreCase(Globals.decrementVariableOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                DecrementVariableProcedure procedure = new DecrementVariableProcedure();
                procedure.addParameter(parameterFactory.getParameter(variable));
                CallProcedureCommand command = new CallProcedureCommand(procedure);

                script.addCommand(command);
                
            } else if (commandName.equalsIgnoreCase(Globals.conditionOperator)) {
                
                try {
                    boolean negative = false;
                    boolean equals = false;
                    boolean greater = false;
                    
                    String leftVariableName = lineScanner.next();
                    Parameter leftParameter = getParameter(parameterFactory, memory, leftVariableName);
                    
                    String token = lineScanner.next();
                    expect(token, Globals.isOperator);
                    
                    token = lineScanner.next();
                    if (token.equalsIgnoreCase(Globals.negationOperator)) {
                        negative = true;
                        token = lineScanner.next();
                    }
                    
                    if (token.equalsIgnoreCase(Globals.equalsOperator))
                        equals = true;
                    else if (token.equalsIgnoreCase(Globals.greaterOperator))
                        greater = true;
                    else
                        throw new ScriptParseException("Invalid operator: " + token);
                    
                    String rightVariableName = lineScanner.next();
                    Parameter rightParameter = getParameter(parameterFactory, memory, rightVariableName);
                    
                    int operation = 0;
                    if (negative) {
                        if (equals) {
                            operation = Condition.NOT_EQUAL;
                        } else if (greater) {
                            operation = Condition.LESS_OR_EQUAL;
                        } else
                            throw new ScriptParseException("Invalid operation");
                    } else {
                        if (equals) {
                            operation = Condition.EQUAL;
                        } else if (greater) {
                            operation = Condition.GREATER;
                        } else
                            throw new ScriptParseException("Invalid operation");
                    }
                    
                    Condition condition = new SimpleCondition(leftParameter, rightParameter, operation);
                    CommandSequence conditionCommandSequence = parseSequence(scanner, memory);
                    
                    ConditionCommand command = new ConditionCommand(condition, conditionCommandSequence);
                    script.addCommand(command);
                    
                } catch (NoSuchElementException e) {
                    throw new ScriptParseException("Missing operands");
                }
                
            } else if (commandName.equalsIgnoreCase(Globals.stackPushOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new StackPushCommand(variable, memory);
                
                script.addCommand(command);
            
            } else if (commandName.equalsIgnoreCase(Globals.stackPopOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new StackPopCommand(variable, memory);
                
                script.addCommand(command);
            
            } else if (commandName.equalsIgnoreCase(Globals.dequeueOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new DequeueCommand(variable, memory);
                
                script.addCommand(command);
                
            } else {
                throw new ScriptParseException("Unknown command " + commandName);
            }
        }
        
        return script;
    }
}
