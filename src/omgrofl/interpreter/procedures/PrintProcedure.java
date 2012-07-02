package omgrofl.interpreter.procedures;

public class PrintProcedure extends AbstractProcedure {

    @Override
    public String getName() {
        return "print";
    }

    @Override
    public Object execute() {
        requireNumberOfParameters(1);

        System.out.println(parameters.get(0).evaluate());
        return null;
    }
}
