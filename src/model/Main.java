package model;

import com.oocourse.specs3.AppRunner;

public class Main {
    public static final boolean DEBUG = false;
    private static final boolean REDIR = false;
    private static final boolean TIMER_ON = false; // TODO: turn off
    private static long timer;
    
    public static void main(String[] args) throws Exception {
        if (TIMER_ON) {
            timer = System.currentTimeMillis();
        }
        AppRunner runner = AppRunner
                .newInstance(MyPath.class, MyRailwaySystem.class);
        runner.run(args);
        if (TIMER_ON) {
            System.out.println("Timer: executed "
                    + (System.currentTimeMillis() - timer) + "ms.");
        }
    }
}
