package Node;

import Analysis.NodeVisitor;

public class MainFunction extends Node {
    private String argc;
    private String argv;
    private FunctionBody body;

    public MainFunction(String argc, String argv, FunctionBody body) {
        this.argc = argc;
        this.argv = argv;
        this.body = body;
    }

    public String getArgc() {
        return argc;
    }

    public String getArgv() {
        return argv;
    }

    public FunctionBody getBody() {
        return body;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            body.accept(v);

            v.postVisit(this);
        }
    }
}
