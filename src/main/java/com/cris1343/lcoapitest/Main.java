package com.cris1343.lcoapitest;

import com.cris1343.lcoapitest.gui.windows.Log;
import com.cris1343.lcoapitest.gui.windows.MainMenu;

public class Main {
    public static MainMenu mainMenu;
    public static Log log;

    public static void main(String[] args) {
        Config.initialize();

        mainMenu = new MainMenu();
        log = new Log();

        Config.load();
    }
}
