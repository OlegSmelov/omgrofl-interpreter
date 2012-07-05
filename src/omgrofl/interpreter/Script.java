package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class Script {
    
    protected CommandSequence commandSequence;

    Script(CommandSequence commandSequence) {
        this.commandSequence = commandSequence;
    }
    
    public void run() throws ScriptRuntimeException {
        try {
            commandSequence.run();
        } catch (ScriptInterruptedException e) {
        } catch (ScriptExitException e) {
        }
    }

    @Override
    public String toString() {
        return commandSequence.toString();
    }
}
