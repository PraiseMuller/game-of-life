package core.inputs;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;

    private boolean[] mouseButtonPressed = new boolean[3];
    private boolean isDragging;

    private MouseListener(){
        scrollX = 0;
        scrollY = 0;
        xPos = 0;
        yPos = 0;
        lastX = 0;
        lastY = 0;
    }

    public static MouseListener get(){
        //mouse listener instance
        if(MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
//        glfwSetCursorPos(Window.getWindow(), Constants.WIN_WIDTH/2f, Constants.WIN_HEIGHT/2f);
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){

        if(action == GLFW_PRESS){
            get().mouseButtonPressed[button] = true;
        }
        else if(action == GLFW_RELEASE){
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void scrollCallback(long window, double xOffset, double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX(){
        return (float) get().xPos;
    }

    public static float getY(){
        return (float) get().yPos;
    }

    public static float getDx(){
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy(){
        return (float) (get().lastY - get().yPos);
    }

    public static float scrollY(){
        return (float) get().scrollY;
    }

    public static float scrollX(){
        return (float) get().scrollX;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        return get().mouseButtonPressed[button];
    }
}