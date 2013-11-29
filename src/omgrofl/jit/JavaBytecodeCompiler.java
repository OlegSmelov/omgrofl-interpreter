package omgrofl.jit;

import java.util.HashSet;
import omgrofl.Globals;
import omgrofl.interpreter.Command;
import omgrofl.interpreter.CommandSequence;
import omgrofl.interpreter.Condition;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Procedure;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.SimpleCondition;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.commands.AssignmentCommand;
import omgrofl.interpreter.commands.BreakCommand;
import omgrofl.interpreter.commands.CallProcedureCommand;
import omgrofl.interpreter.commands.ConditionCommand;
import omgrofl.interpreter.commands.DequeueCommand;
import omgrofl.interpreter.commands.ExitCommand;
import omgrofl.interpreter.commands.InfiniteLoopCommand;
import omgrofl.interpreter.commands.ReadCharacterCommand;
import omgrofl.interpreter.commands.SleepCommand;
import omgrofl.interpreter.commands.StackPopCommand;
import omgrofl.interpreter.commands.StackPushCommand;
import omgrofl.interpreter.parameters.ObjectParameter;
import omgrofl.interpreter.parameters.VariableParameter;
import omgrofl.interpreter.procedures.DecrementVariableProcedure;
import omgrofl.interpreter.procedures.IncrementVariableProcedure;
import omgrofl.interpreter.procedures.PrintCharacterProcedure;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JavaBytecodeCompiler implements Opcodes {

    private ClassWriter classWriter;
    private MethodVisitor methodVisitor;
    private String className;
    private Label lastLoopEnd;
    
    private HashSet<String> definedVariables;
    private boolean printMethodDefined, readMethodDefined;
    private boolean stackDefined;
    
    private MethodVisitor staticBlockVisitor;
    
    public JavaBytecodeCompiler(String className) {
        this.className = className;
    }
    
    public void visit(Script script) throws JavaBytecodeCompilerException {
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        definedVariables = new HashSet<String>();
        printMethodDefined = false;
        readMethodDefined = false;
        stackDefined = false;
        
        classWriter.visit(49, ACC_PUBLIC + ACC_SUPER, className, null,
                "java/lang/Object", null);
        
        // Class constructor
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        
        // Main method
        {
            staticBlockVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
                    "<clinit>", "()V", null, null);
        
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
                    "([Ljava/lang/String;)V", null, null);
            
            lastLoopEnd = null;
            visit(script.getCommandSequence());
            
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 0);
            methodVisitor.visitEnd();
            
            staticBlockVisitor.visitInsn(RETURN);
            staticBlockVisitor.visitMaxs(0, 0);
            staticBlockVisitor.visitEnd();
        }
    }
    
    private void visit(CommandSequence commandSequence) throws JavaBytecodeCompilerException {
        Command[] commands = commandSequence.getCommands();
        for (Command command : commands) {
            visit(command);
        }
    }
    
    private void visit(Command command) throws JavaBytecodeCompilerException {
        try {
           if (command instanceof InfiniteLoopCommand) {
            visit((InfiniteLoopCommand) command);
            } else if (command instanceof BreakCommand) {
                visit((BreakCommand) command);
            } else if (command instanceof AssignmentCommand) {
                visit((AssignmentCommand) command);
            } else if (command instanceof CallProcedureCommand) {
                visit((CallProcedureCommand) command);
            } else if (command instanceof ConditionCommand) {
                visit((ConditionCommand) command);
            } else if (command instanceof StackPushCommand) {
                visit((StackPushCommand) command);
            } else if (command instanceof StackPopCommand) {
                visit((StackPopCommand) command);
            } else if (command instanceof DequeueCommand) {
                visit((DequeueCommand) command);
            } else if (command instanceof ExitCommand) {
                visit((ExitCommand) command);
            } else if (command instanceof ReadCharacterCommand) {
                visit((ReadCharacterCommand) command);
            } else if (command instanceof SleepCommand) {
                visit((SleepCommand) command);
            } else
                throw new JavaBytecodeCompilerException("JIT is unaware of this command, " +
                        "try using interpreter mode");
        } catch (JavaBytecodeCompilerException exception) {
            if (exception.getLine() == null)
                exception.setLine(command.getSourceCodeLine());
            throw exception;
        }
    }
    
    private void visit(InfiniteLoopCommand loopCommand) throws JavaBytecodeCompilerException {
        CommandSequence commandSequence = loopCommand.getCommandSequence();
        Command[] commands = commandSequence.getCommands();
        
        Label startLabel = new Label(),
                endLabel = new Label();

        Label previousLoopEnd = lastLoopEnd;
        lastLoopEnd = endLabel;
        methodVisitor.visitLabel(startLabel);
        
        for (Command command : commands) {
            visit(command);
        }

        methodVisitor.visitJumpInsn(GOTO, startLabel);
        methodVisitor.visitLabel(endLabel);
        lastLoopEnd = previousLoopEnd;
    }
    
    private void visit(BreakCommand breakCommand) {
        methodVisitor.visitJumpInsn(GOTO, lastLoopEnd);
    }
    
    private void visit(AssignmentCommand assignmentCommand) throws JavaBytecodeCompilerException {
        Variable variable = assignmentCommand.getVariable();
        Parameter parameter = assignmentCommand.getValue();
        
        visitParameterLoad(parameter);
        visitVariableDefinition(variable, 0);
        methodVisitor.visitFieldInsn(PUTSTATIC, className,
                variable.getName().toLowerCase(), "C");
    }
    
    private void visit(CallProcedureCommand callProcedureCommand) throws JavaBytecodeCompilerException {
        Procedure procedure = callProcedureCommand.getProcedure();
        
        try
        {
            if (procedure instanceof IncrementVariableProcedure) {
                visitProcedure((IncrementVariableProcedure) procedure);
            } else if (procedure instanceof DecrementVariableProcedure) {
                visitProcedure((DecrementVariableProcedure) procedure);
            } else if (procedure instanceof PrintCharacterProcedure) {
                visitProcedure((PrintCharacterProcedure) procedure);
            } else
                throw new JavaBytecodeCompilerException("JIT is unaware of this procedure, " +
                            "try using interpreter mode");
        } catch (JavaBytecodeCompilerException exception) {
            if (exception.getLine() == null)
                exception.setLine(callProcedureCommand.getSourceCodeLine());
            throw exception;
        }
    }
    
    private void visit(ConditionCommand conditionCommand) throws JavaBytecodeCompilerException {
        Condition condition = conditionCommand.getCondition();
        Command[] commands = conditionCommand.getCommandSequence().getCommands();
        
        if (condition instanceof SimpleCondition) {
            SimpleCondition simpleCondition = (SimpleCondition) condition;
            int operation = simpleCondition.getOperation();
            Parameter leftParameter = simpleCondition.getLeftParameter(),
                     rightParameter = simpleCondition.getRightParameter();
            
            Label conditionEnd = new Label();
            visitParameterLoad(leftParameter);
            visitParameterLoad(rightParameter);
            
            switch (operation) {
                case Condition.EQUAL:
                    methodVisitor.visitJumpInsn(IF_ICMPNE, conditionEnd);
                    break;
                case Condition.GREATER:
                    methodVisitor.visitJumpInsn(IF_ICMPLE, conditionEnd);
                    break;
                case Condition.GREATER_OR_EQUAL:
                    methodVisitor.visitJumpInsn(IF_ICMPLT, conditionEnd);
                    break;
                case Condition.LESS:
                    methodVisitor.visitJumpInsn(IF_ICMPGE, conditionEnd);
                    break;
                case Condition.LESS_OR_EQUAL:
                    methodVisitor.visitJumpInsn(IF_ICMPGT, conditionEnd);
                    break;
                case Condition.NOT_EQUAL:
                    methodVisitor.visitJumpInsn(IF_ICMPEQ, conditionEnd);
                    break;
                default:
                    throw new JavaBytecodeCompilerException(
                            conditionCommand.getSourceCodeLine(),
                            "Condition not supported");
            }
            
            for (Command command : commands) {
                visit(command);
            }
            
            methodVisitor.visitLabel(conditionEnd);
        } else
            throw new JavaBytecodeCompilerException(conditionCommand.getSourceCodeLine(),
                    "This type of condition is not supported");
    }
    
    private void visit(StackPushCommand stackPushCommand) throws JavaBytecodeCompilerException {
        Variable variable = stackPushCommand.getVariable();

        visitStackDefinition();
        
        methodVisitor.visitFieldInsn(GETSTATIC, className, "stack",
                "Ljava/util/Deque;");
        visitVariableLoad(variable);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
                "valueOf", "(I)Ljava/lang/Integer;");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Deque",
                "push", "(Ljava/lang/Object;)V");
    }
    
    private void visit(StackPopCommand stackPopCommand) throws JavaBytecodeCompilerException {
        Variable variable = stackPopCommand.getVariable();
        
        visitStackDefinition();
        
        methodVisitor.visitFieldInsn(GETSTATIC, className, "stack",
                "Ljava/util/Deque;");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Deque",
                "removeFirst", "()Ljava/lang/Object;");
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer",
                "intValue", "()I");
        
        visitVariableDefinition(variable, 0);
        methodVisitor.visitFieldInsn(PUTSTATIC, className,
                variable.getName().toLowerCase(), "C");
    }
    
    private void visit(DequeueCommand dequeueCommand) throws JavaBytecodeCompilerException {
        Variable variable = dequeueCommand.getVariable();
        
        visitStackDefinition();
        
        methodVisitor.visitFieldInsn(GETSTATIC, className, "stack",
                "Ljava/util/Deque;");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Deque",
                "removeLast", "()Ljava/lang/Object;");
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer",
                "intValue", "()I");
        
        visitVariableDefinition(variable, 0);
        methodVisitor.visitFieldInsn(PUTSTATIC, className,
                variable.getName().toLowerCase(), "C");
    }
    
    private void visit(ExitCommand exitCommand) {
        methodVisitor.visitInsn(RETURN);
    }
    
    private void visit(ReadCharacterCommand readCharacterCommand) {
        if (!readMethodDefined) {
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
                    "readCharacter", "()C", null, null);
            
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in",
                    "Ljava/io/InputStream;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/InputStream",
                    "read", "()I");
            visitAdjustIntegerValue(mv);
            
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
            
            readMethodDefined = true;
        }
        
        Variable variable = readCharacterCommand.getVariable();
        visitVariableDefinition(variable, 0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, className, "readCharacter",
                "()C");
        methodVisitor.visitFieldInsn(PUTSTATIC, className,
            variable.getName().toLowerCase(), "C");
    }
    
    private void visit(SleepCommand sleepCommand) throws JavaBytecodeCompilerException {
        Parameter parameter = sleepCommand.getParameter();
        
        visitParameterLoad(parameter);
        methodVisitor.visitInsn(I2L);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep",
                "(J)V");
    }
    
    /**
     * Takes a value off the stack and applies 0xFF mask to it. This is
     * required so that the value always stays within the allowed range (0-255).
     */
    private void visitAdjustIntegerValue(MethodVisitor mv) {
        mv.visitIntInsn(SIPUSH, 0xFF);
        mv.visitInsn(IAND);
    }
    
    private void visitAdjustIntegerValue() {
        visitAdjustIntegerValue(methodVisitor);
    }
    
    /**
     * Define stack if not already defined. If defined, nothing is done, and
     * not exception is thrown.
     */
    private void visitStackDefinition() {
        if (stackDefined) {
            return;
        }
        
        classWriter.visitField(ACC_PUBLIC + ACC_STATIC, "stack",
                "Ljava/util/Deque;", null, null).visitEnd();

        staticBlockVisitor.visitTypeInsn(NEW, "java/util/LinkedList");
        staticBlockVisitor.visitInsn(DUP);
        staticBlockVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedList",
                "<init>", "()V");
        staticBlockVisitor.visitFieldInsn(PUTSTATIC, className, "stack",
                "Ljava/util/Deque;");
        
        stackDefined = true;
    }
    
    private void visitParameterLoad(Parameter parameter) throws JavaBytecodeCompilerException {
        if (parameter instanceof ObjectParameter) {
            Object value = parameter.evaluate();
            Integer intValue = (Integer) value;
            switch (intValue) {
                case 0:
                    methodVisitor.visitInsn(ICONST_0);
                    break;
                case 1:
                    methodVisitor.visitInsn(ICONST_1);
                    break;
                case 2:
                    methodVisitor.visitInsn(ICONST_2);
                    break;
                case 3:
                    methodVisitor.visitInsn(ICONST_3);
                    break;
                case 4:
                    methodVisitor.visitInsn(ICONST_4);
                    break;
                case 5:
                    methodVisitor.visitInsn(ICONST_5);
                    break;
                default:
                    methodVisitor.visitIntInsn(SIPUSH, intValue);
                    break;
            }
            
        } else if (parameter instanceof VariableParameter) {
            VariableParameter variableParameter = (VariableParameter) parameter;
            Variable valueVariable = variableParameter.getVariable();
            
            if (!definedVariables.contains(valueVariable.getName().toLowerCase()))
                throw new JavaBytecodeCompilerException("Trying to use an undefined variable");
            
            visitVariableLoad(valueVariable);
        } else
            throw new JavaBytecodeCompilerException("Parameter type is not supported");
    }
    
    private void visitIncrementDecrementProcedure(Variable variable, boolean increment) throws JavaBytecodeCompilerException {
        if (!definedVariables.contains(variable.getName().toLowerCase()))
            throw new JavaBytecodeCompilerException("Trying to use an undefined variable"); 

        visitVariableLoad(variable);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitInsn(increment ? IADD : ISUB);
        visitAdjustIntegerValue();
        methodVisitor.visitFieldInsn(PUTSTATIC, className,
            variable.getName().toLowerCase(), "C");
    }
    
    private void visitProcedure(IncrementVariableProcedure procedure) throws JavaBytecodeCompilerException {
        Parameter parameter = procedure.getParameter(0);
        if (parameter instanceof VariableParameter) {
            Variable variable = ((VariableParameter) parameter).getVariable();
            visitIncrementDecrementProcedure(variable, true);
        }
        else
            throw new JavaBytecodeCompilerException("Bad parameter");
    }
    
    private void visitProcedure(DecrementVariableProcedure procedure) throws JavaBytecodeCompilerException {
        Parameter parameter = procedure.getParameter(0);
        if (parameter instanceof VariableParameter) {
            Variable variable = ((VariableParameter) parameter).getVariable();
            visitIncrementDecrementProcedure(variable, false);
        }
        else
            throw new JavaBytecodeCompilerException("Bad parameter");
    }
    
    private void visitProcedure(PrintCharacterProcedure procedure) throws JavaBytecodeCompilerException {
        Parameter parameter = procedure.getParameter(0);
        if (parameter instanceof VariableParameter) {
            Variable variable = ((VariableParameter) parameter).getVariable();
            visitPrintMethod(variable);
        }
        else
            throw new JavaBytecodeCompilerException("Bad parameter");
    }
    
    private void visitPrintMethod(Variable variable) throws JavaBytecodeCompilerException {
        if (!printMethodDefined) {
            // printStream field
            classWriter.visitField(ACC_PUBLIC + ACC_STATIC, "printStream",
                    "Ljava/io/PrintStream;", null, null).visitEnd();

            // static block (printStream initializer)
            staticBlockVisitor.visitTypeInsn(NEW, "java/io/PrintStream");
            staticBlockVisitor.visitInsn(DUP);
            staticBlockVisitor.visitFieldInsn(GETSTATIC, "java/lang/System",
                    "out", "Ljava/io/PrintStream;");
            staticBlockVisitor.visitInsn(ICONST_1);
            staticBlockVisitor.visitLdcInsn(Globals.defaultEncoding);
            staticBlockVisitor.visitMethodInsn(INVOKESPECIAL, "java/io/PrintStream",
                    "<init>", "(Ljava/io/OutputStream;ZLjava/lang/String;)V");
            staticBlockVisitor.visitFieldInsn(PUTSTATIC, className, "printStream",
                    "Ljava/io/PrintStream;");
            
            // method
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC,
                    "printCharacter", "(C)V", null, null);
            
            mv.visitFieldInsn(GETSTATIC, className, "printStream", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(C)V");
            
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
            
            printMethodDefined = true;
        }
        
        visitVariableLoad(variable);
        methodVisitor.visitMethodInsn(INVOKESTATIC, className, "printCharacter", "(C)V");
    }
    
    private void visitVariableDefinition(Variable variable, Object value) {
        String varName = variable.getName().toLowerCase();
        if (!definedVariables.contains(varName)) {
            classWriter.visitField(ACC_PRIVATE + ACC_STATIC, varName,
                    "C", null, value).visitEnd();
            definedVariables.add(varName);
        }
    }
    
    /**
     * Adds the load variable opcodes
     * @param variable Variable to load
     */
    private void visitVariableLoad(Variable variable) throws JavaBytecodeCompilerException {
        String varName = variable.getName().toLowerCase();
        if (!definedVariables.contains(varName))
            throw new JavaBytecodeCompilerException("Variable is not defined");
        methodVisitor.visitFieldInsn(GETSTATIC, className, varName, "C");
    }
    
    public byte[] getBytecode() {
        return classWriter.toByteArray();
    }
}
