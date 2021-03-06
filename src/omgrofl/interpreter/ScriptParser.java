package omgrofl.interpreter;

import omgrofl.Globals;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import omgrofl.interpreter.commands.*;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.factories.ParameterFactory;
import omgrofl.interpreter.procedures.DecrementVariableProcedure;
import omgrofl.interpreter.procedures.IncrementVariableProcedure;
import omgrofl.interpreter.procedures.PrintCharacterProcedure;

public class ScriptParser {

    private int linesParsed;

    public Script parse(InputStream input, Memory memory) throws ScriptParseException {
        linesParsed = 0;
        Scanner scanner = new Scanner(input);
        CommandSequence commandSequence = parseSequence(scanner, memory);
        return new Script(commandSequence);
    }

    public Script parse(String input, Memory memory) throws ScriptParseException {
        linesParsed = 0;
        Scanner scanner = new Scanner(input);
        CommandSequence commandSequence = parseSequence(scanner, memory);
        return new Script(commandSequence);
    }

    private Variable readVariable(Memory memory, Scanner scanner) throws ScriptParseException {
        try {
            String variableName = scanner.next();
            if (!variableName.toLowerCase().matches(Globals.variablePattern)) {
                throw new ScriptParseException(linesParsed, variableName
                        + " is not a valid variable name");
            }

            Variable variable = new Variable(memory, variableName);
            return variable;

        } catch (NoSuchElementException e) {
            throw new ScriptParseException(linesParsed, "Missing variable");
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
                if (Globals.validValue(integerValue)) {
                    return parameterFactory.getParameter(integerValue);
                } else {
                    throw new ScriptParseException(linesParsed, "Invalid value: " + integerValue);
                }
            } catch (NumberFormatException e) {
                throw new ScriptParseException(linesParsed, "Wrong number format: " + name);
            }
        } else {
            throw new ScriptParseException(linesParsed, "Invalid parameter: " + name);
        }
    }

    public String readNext(Scanner scanner) throws ScriptParseException {
        try {
            return scanner.next();
        } catch (NoSuchElementException e) {
            throw new ScriptParseException(linesParsed, "Missing operand");
        }
    }

    private void expect(String value, String expected) throws ScriptParseException {
        if (!value.equalsIgnoreCase(expected)) {
            throw new ScriptParseException(linesParsed, expected + " expected, "
                    + value + " found");
        }
    }

    /**
     * Throws an exception if scanner still has tokens.
     */
    private void checkEnd(Scanner scanner) throws ScriptParseException {
        if (scanner.hasNext()) {
            throw new ScriptParseException(linesParsed, "Unexpected token '" + scanner.next()
                    + "', probably it shouldn't be there");
        }
    }

    private CommandSequence parseSequence(Scanner scanner, Memory memory) throws ScriptParseException {
        return parseSequence(scanner, memory, true, false);
    }

    /**
     * @param isRoot root is being parsed (not statements inside a loop and
     * such).
     */
    private CommandSequence parseSequence(Scanner scanner, Memory memory, boolean isRoot, boolean insideLoop)
            throws ScriptParseException {
        CommandSequence script = new CommandSequence();
        ParameterFactory parameterFactory = new ParameterFactory();
        boolean endOperatorFound = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            linesParsed++;

            if (isRoot && linesParsed == 1 && line.startsWith("#!")) {
                // it's a shebang, ignore the whole line
                continue;
            }

            Scanner lineScanner = new Scanner(line);

            // Empty line, skip
            if (!lineScanner.hasNext()) {
                continue;
            }

            String commandName = lineScanner.next();

            if (commandName.equalsIgnoreCase(Globals.endOperator)) {
                // end of script
                checkEnd(lineScanner);

                if (isRoot) {
                    throw new ScriptParseException(linesParsed, "Unmatched end operator (brb)");
                }

                endOperatorFound = true;
                break;
            } else if (commandName.equalsIgnoreCase(Globals.commentOperator)) {
                // comment
                continue;
            } else if (commandName.equalsIgnoreCase(Globals.loopOperator)) {
                checkEnd(lineScanner);

                // infinite loop (until broken)
                CommandSequence loopCommandSequence = parseSequence(scanner, memory, false, true);
                InfiniteLoopCommand loop = new InfiniteLoopCommand(loopCommandSequence);
                script.addCommand(loop, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.forLoopOperator)) {
                try {
                    String varName = lineScanner.next();
                    if (!varName.toLowerCase().matches(Globals.variablePattern)) {
                        throw new ScriptParseException(
                                linesParsed,
                                "Invalid variable name " + varName);
                    }

                    String assignmentOp = lineScanner.next();
                    if (!assignmentOp.equals(Globals.isOperator)) {
                        throw new ScriptParseException(
                                linesParsed,
                                "Expected " + Globals.isOperator + ", found " + assignmentOp);
                    }

                    Parameter initialValueParameter;

                    String initialValue = lineScanner.next();
                    if (initialValue.toLowerCase().matches(Globals.numberPattern)) {
                        Integer initialValueInt = Integer.parseInt(initialValue);

                        if (!Globals.validValue(initialValueInt)) {
                            throw new ScriptParseException(
                                    linesParsed,
                                    "Value " + initialValueInt + " is out of bounds (" + Globals.minAllowedValue
                                    + "-" + Globals.maxAllowedValue + ")");
                        }

                        initialValueParameter = parameterFactory.getParameter(initialValueInt);
                    } else if (initialValue.toLowerCase().matches(Globals.variablePattern)) {
                        Variable variable = new Variable(memory, initialValue);
                        initialValueParameter = parameterFactory.getParameter(variable);
                    } else {
                        throw new ScriptParseException(
                                linesParsed,
                                "Value " + initialValue + " has to be a variable or an integer");
                    }

                    String toOp = lineScanner.next();
                    if (!toOp.equals(Globals.toOperator)) {
                        throw new ScriptParseException(
                                linesParsed,
                                "Expected " + Globals.toOperator + ", found " + toOp);
                    }

                    Parameter endValueParameter;

                    String endValue = lineScanner.next();
                    if (endValue.toLowerCase().matches(Globals.numberPattern)) {
                        Integer endValueInt = Integer.parseInt(endValue);

                        if (!Globals.validValue(endValueInt)) {
                            throw new ScriptParseException(
                                    linesParsed,
                                    "Value " + endValueInt + " is out of bounds (" + Globals.minAllowedValue
                                    + "-" + Globals.maxAllowedValue + ")");
                        }

                        endValueParameter = parameterFactory.getParameter(endValueInt);
                    } else if (endValue.toLowerCase().matches(Globals.variablePattern)) {
                        Variable variable = new Variable(memory, endValue);
                        endValueParameter = parameterFactory.getParameter(variable);
                    } else {
                        throw new ScriptParseException(
                                linesParsed,
                                "Value " + endValue + " has to be a variable or an integer");
                    }

                    checkEnd(lineScanner);

                    // for loop
                    Variable variable = new Variable(memory, varName);
                    CommandSequence loopCommandSequence = parseSequence(scanner, memory, false, true);

                    ForLoopCommand loop = new ForLoopCommand(
                            variable,
                            initialValueParameter,
                            endValueParameter,
                            loopCommandSequence
                    );

                    script.addCommand(loop, linesParsed);
                } catch (NoSuchElementException e) {
                    throw new ScriptParseException(linesParsed, "Missing operands");
                }
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
                            if (!Globals.validValue(newValue)) {
                                throw new ScriptParseException(
                                        linesParsed,
                                        "Value " + newValue + " is out of bounds (" + Globals.minAllowedValue
                                        + "-" + Globals.maxAllowedValue + ")");
                            }
                            valueParameter = parameterFactory.getParameter(newValue);
                        } else if (value.toLowerCase().matches(Globals.variablePattern)) {
                            Variable paramVariable = new Variable(memory, value);
                            valueParameter = parameterFactory.getParameter(paramVariable);
                        } else {
                            throw new ScriptParseException(linesParsed, "Unrecognized operand " + value);
                        }
                    } else if (operator.equalsIgnoreCase("to") && value.equalsIgnoreCase("/dev/null")) {
                        newValue = 0;
                        valueParameter = parameterFactory.getParameter(newValue);
                    } else {
                        throw new ScriptParseException(linesParsed, "Unrecognized operation with variable " + varName);
                    }

                } catch (NoSuchElementException e) {
                    throw new ScriptParseException(linesParsed, "Missing operands");
                }

                AssignmentCommand assignmentCommand = new AssignmentCommand(variable, valueParameter);
                script.addCommand(assignmentCommand, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.breakOperator)) {
                if (!insideLoop) {
                    throw new ScriptParseException(linesParsed, "Break operator outside of a loop is not allowed");
                }
                BreakCommand breakCommand = new BreakCommand();
                script.addCommand(breakCommand, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.printCharacterOperator)) {

                try {
                    String variableName = lineScanner.next();

                    if (!variableName.toLowerCase().matches(Globals.variablePattern)) {
                        throw new ScriptParseException(linesParsed, variableName
                                + " is not a valid variable name");
                    }

                    Variable variable = new Variable(memory, variableName);
                    Parameter variableParameter = parameterFactory.getParameter(variable);

                    PrintCharacterProcedure printCharacterProcedure = new PrintCharacterProcedure();
                    printCharacterProcedure.addParameter(variableParameter);

                    CallProcedureCommand command = new CallProcedureCommand(printCharacterProcedure);

                    script.addCommand(command, linesParsed);
                } catch (NoSuchElementException e) {
                    throw new ScriptParseException(linesParsed, "Missing operands");
                }

            } else if (commandName.equalsIgnoreCase(Globals.readCharacterOperator)) {

                Variable variable = readVariable(memory, lineScanner);
                Command command = new ReadCharacterCommand(variable);
                script.addCommand(command, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.exitOperator)) {
                Command command = new ExitCommand();
                script.addCommand(command, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.sleepOperator)) {
                try {
                    String durationString = lineScanner.next();
                    Parameter durationParameter = getParameter(parameterFactory, memory, durationString);
                    Command command = new SleepCommand(durationParameter);
                    script.addCommand(command, linesParsed);

                } catch (NoSuchElementException e) {
                    throw new ScriptParseException(linesParsed, "Missing operands");
                }

            } else if (commandName.equalsIgnoreCase(Globals.incrementVariableOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                IncrementVariableProcedure procedure = new IncrementVariableProcedure();
                procedure.addParameter(parameterFactory.getParameter(variable));
                CallProcedureCommand command = new CallProcedureCommand(procedure);

                script.addCommand(command, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.decrementVariableOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                DecrementVariableProcedure procedure = new DecrementVariableProcedure();
                procedure.addParameter(parameterFactory.getParameter(variable));
                CallProcedureCommand command = new CallProcedureCommand(procedure);

                script.addCommand(command, linesParsed);

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

                    if (token.equalsIgnoreCase(Globals.equalsOperator)) {
                        equals = true;
                    } else if (token.equalsIgnoreCase(Globals.greaterOperator)) {
                        greater = true;
                    } else {
                        throw new ScriptParseException(linesParsed, "Invalid operator: " + token);
                    }

                    String rightVariableName = lineScanner.next();
                    Parameter rightParameter = getParameter(parameterFactory, memory, rightVariableName);

                    int operation = 0;
                    if (negative) {
                        if (equals) {
                            operation = Condition.NOT_EQUAL;
                        } else if (greater) {
                            operation = Condition.LESS_OR_EQUAL;
                        } else {
                            throw new ScriptParseException(linesParsed, "Invalid operation");
                        }
                    } else {
                        if (equals) {
                            operation = Condition.EQUAL;
                        } else if (greater) {
                            operation = Condition.GREATER;
                        } else {
                            throw new ScriptParseException(linesParsed, "Invalid operation");
                        }
                    }

                    Condition condition = new SimpleCondition(leftParameter, rightParameter, operation);
                    CommandSequence conditionCommandSequence = parseSequence(scanner, memory, false, insideLoop);

                    ConditionCommand command = new ConditionCommand(condition, conditionCommandSequence);
                    script.addCommand(command, linesParsed);

                } catch (NoSuchElementException e) {
                    throw new ScriptParseException(linesParsed, "Missing operands");
                }

            } else if (commandName.equalsIgnoreCase(Globals.stackPushOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new StackPushCommand(variable, memory);

                script.addCommand(command, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.stackPopOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new StackPopCommand(variable, memory);

                script.addCommand(command, linesParsed);

            } else if (commandName.equalsIgnoreCase(Globals.dequeueOperator)) {
                Variable variable = readVariable(memory, lineScanner);
                Command command = new DequeueCommand(variable, memory);

                script.addCommand(command, linesParsed);

            } else {
                throw new ScriptParseException(linesParsed, "Unknown command " + commandName);
            }

            // Throw an exception if there are tokens left. They haven't been
            // used, therefore they were unexpected.
            checkEnd(lineScanner);
        }

        if (!isRoot && !endOperatorFound) {
            throw new ScriptParseException(linesParsed, "End operator (brb) expected, none found");
        }

        return script;
    }
}
