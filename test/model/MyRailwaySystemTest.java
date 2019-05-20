package model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyRailwaySystemTest {
    private MyRailwaySystem myRailwaySystem;
    
    @Before
    public void setUp() {
        myRailwaySystem = new MyRailwaySystem();
        myRailwaySystem.addPath(new MyPath(1, 2, 3, 4));
        myRailwaySystem.addPath(new MyPath(5, 6));
        myRailwaySystem.addPath(new MyPath(1, 2, 3, 4));
        myRailwaySystem.addPath(new MyPath(7, 8));
    }
    
    @Test
    public void getPathId() throws Exception {
        assertEquals(1, myRailwaySystem.getPathId(new MyPath(1, 2, 3, 4)));
        assertEquals(2, myRailwaySystem.getPathId(new MyPath(5, 6)));
        assertEquals(3, myRailwaySystem.getPathId(new MyPath(7, 8)));
    }
}