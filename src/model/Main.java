package model;

import com.oocourse.specs2.AppRunner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static final boolean DEBUG = false;
    private static final boolean REDIR = false;
    private static final boolean TIMER_ON = false; // TODO: turn off
    private static long timer;
    
    public static void main(String[] args) throws Exception {
        if (REDIR) {
            System.setIn(new FileInputStream("./test/test.txt"));
            System.setOut(new PrintStream(
                    new FileOutputStream("./test/out.txt")));
        }
        if (TIMER_ON) {
            timer = System.currentTimeMillis();
        }
        AppRunner runner = AppRunner
                .newInstance(MyPath.class, MyGraph.class);
        runner.run(args);
        if (TIMER_ON) {
            System.out.println("Timer: executed "
                    + (System.currentTimeMillis() - timer) + "ms.");
        }
    }
}
