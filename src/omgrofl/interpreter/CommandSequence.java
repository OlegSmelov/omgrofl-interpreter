package omgrofl.interpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class CommandSequence {
    
    protected List<Command> commands = new ArrayList<Command>();
    
    public void addCommand(Command command) {
        commands.add(command);
    }
    
    public void addCommands(Collection<Command> commands) {
        this.commands.addAll(commands);
    }
    
    public void clearCommands() {
        commands.clear();
    }
    
    public Command[] getCommands() {
        return commands.toArray(new Command[0]);
    }
    
    public void run() throws ScriptInterruptedException, ScriptExitException {
        for (Command command : commands)
            command.execute();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (Command command : commands) {
            sb.append(command.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
