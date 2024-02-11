package core.utils;

import core.engine.Scene;
import core.inputs.KeyListener;
import core.inputs.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;

import static core.utils.Constants.WIN_HEIGHT;
import static core.utils.Constants.WIN_WIDTH;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long window;
    private static Scene sceneInstance = null;
    private static Window instance = null;

    private Window(){
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new RuntimeException("Failed to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        window = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, Constants.WIN_TITLE, NULL, NULL);

        if(window == NULL){
            throw new RuntimeException("Failed to create window.");
        }

        // [...legs]

        //callbacks and such...
        glfwSetKeyCallback(window, KeyListener::keyCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::scrollCallback);
        glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        glfwSetWindowSizeCallback(window, (w, width, height)->{
            setWidth(width);
            setHeight(height);
            glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        });

        glfwMakeContextCurrent(window);

        if(Constants.V_SYNC){
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_MULTISAMPLE);

        sceneInstance = new Scene();
    }

    public static Window get(){
        if(Window.instance == null){
            Window.instance = new Window();
        }
        return Window.instance;
    }

    public void run(){

        float dt = 0;
        float startTime = Time.get();
        float endTime;
        float div = 0.0f;

        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();

            if(dt > 0){
                glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

                if(div % 20 == 0 && StateMachine.getCurrentState() == StateMachine.states.PLAY) {
                    sceneInstance.update(dt);
                }
                if(div % 20 == 0) sceneInstance.updateInputs(dt);
                sceneInstance.render();
                sceneInstance.renderGUI(dt);
            }

            glfwSwapBuffers(window);

            endTime = Time.get();
            dt = endTime - startTime;
            startTime = endTime;
            div += 1.0f;
        }

        cleanup();
    }

    public static void cleanup(){
        //cleanup
        sceneInstance.dispose();
        glfwFreeCallbacks(window);
        glfwSetErrorCallback(null).free();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static long getWindow(){
        return window;
    }
    public void setWidth(int w){
        WIN_WIDTH = w;
    }
    public void setHeight(int h){
        WIN_HEIGHT = h;
    }

    public static int getWidth(){
        return WIN_WIDTH;
    }
    public static int getHeight(){
        return WIN_HEIGHT;
    }

}
