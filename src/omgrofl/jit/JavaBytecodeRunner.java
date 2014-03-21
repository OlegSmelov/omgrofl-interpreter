package omgrofl.jit;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;

public class JavaBytecodeRunner extends ClassLoader {
    
    private JavaBytecodeRunner() { }
    
    public static void run(byte[] bytecode, String className) {
        try {
            JavaBytecodeRunner bytecodeRunner = new JavaBytecodeRunner();
            Class newClass = bytecodeRunner.defineClass(className, bytecode, 0, bytecode.length);
            Method[] methods = newClass.getMethods();
            for (Method method : methods) {
                if ("main".equals(method.getName())) {
                    method.invoke(null, new Object[] {null});
                    break;
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JavaBytecodeRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JavaBytecodeRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JavaBytecodeRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
