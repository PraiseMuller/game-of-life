package core.inputs;

import core.engine.Scene;
import core.utils.StateMachine;
import core.utils.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private Input(){}

    public static void update(float dt, Scene scene){

        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            StateMachine.changeState();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_R)){
            scene.reloadGrid();
        }
    }

    public static boolean mouseLeftClicked(){
        return MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1);
    }

    public static Vector2f getMousePosition(){
        return new Vector2f(MouseListener.getX(), MouseListener.getY());
    }

}
