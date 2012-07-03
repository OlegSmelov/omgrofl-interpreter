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
        return parse(scanner, memory);
    }
    
    public Script parse(String input, Memory memory) throws ScriptParseException {
        Scanner scanner = new Scanner(input);
        return parse(scanner, memory);
    }
    
    private Script parse(Scanner scanner, Memory memory) throws ScriptParseException {
        Script script = new Script();
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
                Script loopScript = parse(scanner, memory);
                InfiniteLoopCommand loop = new InfiniteLoopCommand(loopScript);
                script.addCommand(loop);
            } else if (commandName.toLowerCase().matches(Globals.variablePattern)) {
                String varName = commandName;
                
                Variable variable = new Variable(memory, varName);
                Parameter valueParameter;
                
                try {
                    String operator = lineScanner.next();
                    String value = lineScanner.next();
                    Integer newValue;
                    
                    if (operator.equalsIgnoreCase("iz")) {
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
                Variable variable = readVariable(memory, lineScanner);
                expect(readNext(lineScanner), "iz");
                String comparison = readNext(lineScanner);
                expect(comparison, "liek");
                
                String value = readNext(lineScanner);
                Integer integerValue = Integer.parseInt(value);
                if (!Globals.validValue(integerValue))
                    throw new ScriptParseException("Unacceptable value: " + integerValue);
                
                Script conditionScript = parse(scanner, memory);
                ConditionCommand command = new ConditionCommand(variable, integerValue, conditionScript);
                
                script.addCommand(command);
            } else {
                throw new ScriptParseException("Unknown command " + commandName);
            }
        }
        
        return script;
    }
}
