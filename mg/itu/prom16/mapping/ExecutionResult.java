package mg.itu.prom16.mapping;

public class ExecutionResult {
    private final Object result;
    private final Object[] args;
    private final Mapping mapping;

    public ExecutionResult(Object result, Object[] args, Mapping mapping) {
        this.result = result;
        this.args = args;
        this.mapping = mapping;
    }

    public Object getResult() {
        return result;
    }

    public Object[] getArgs() {
        return args;
    }

    public Mapping getMapping() {
        return mapping;
    }
}
