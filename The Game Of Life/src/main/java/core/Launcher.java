package core;

import core.utils.Window;

public class Launcher {
    public static void main(String args[]){
       Window windowInstance = Window.get();
       windowInstance.run();
    }
}
