package core.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    private static float startTime = (float) glfwGetTime();

    private Time(){}

    public static float get(){
        return (float) (glfwGetTime() - startTime);
    }
}
