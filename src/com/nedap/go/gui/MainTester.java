package com.nedap.go.gui;

/**
 * Example on how to use the GOGUI
 *
 * @author Daan van Beek
 */
public class MainTester {

    public static void main(String[] args) {
        GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, 9);
        gogui.startGUI();
        gogui.setBoardSize(9);

        gogui.addStone(0, 0, false);
        gogui.addStone(0, 2, true);
        gogui.addStone(0, 3, false);
        gogui.addStone(1, 1, true);
        gogui.addStone(1, 2, true);
        gogui.addStone(1, 3, false);
        gogui.addStone(2, 0, true);
        gogui.addStone(2, 1, true);
        gogui.addStone(2, 2, false);
        gogui.addStone(2, 7, false);
        gogui.addStone(2, 8, false);
        gogui.addStone(3, 1, false);
        gogui.addStone(3, 2, false);
        gogui.addStone(3, 3, false);
        gogui.addStone(3, 4, false);
        gogui.addStone(3, 5, false);
        gogui.addStone(3, 6, false);
        gogui.addStone(3, 7, false);
        gogui.addStone(3, 8, true);
        gogui.addStone(4, 0, false);
        gogui.addStone(4, 1, false);
        gogui.addStone(4, 7, true);
        gogui.addStone(4, 8, true);
        gogui.addStone(5, 0, false);
        gogui.addStone(5, 1, true);
        gogui.addStone(5, 2, true);
        gogui.addStone(5, 3, true);
        gogui.addStone(5, 4, true);
        gogui.addStone(5, 5, true);
        gogui.addStone(5, 6, true);
        gogui.addStone(5, 7, true);
        gogui.addStone(6, 0, true);
        gogui.addStone(6, 1, true);
        gogui.addStone(6, 6, true);
        gogui.addStone(6, 7, false);
        gogui.addStone(6, 8, false);
        gogui.addStone(7, 5, true);
        gogui.addStone(7, 6, false);
        gogui.addStone(7, 7, false);
        gogui.addStone(8, 5, true);
        gogui.addStone(8, 6, false);
        gogui.addStone(8, 8, false);
    }
}
