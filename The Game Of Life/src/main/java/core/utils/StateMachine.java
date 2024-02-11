package core.utils;

public class StateMachine {

    public enum states {
        PLAY, STOP,
    }

    private static states currentState = states.STOP;

    private StateMachine(){};

    public static void changeState(){
        if(StateMachine.currentState == states.STOP)
            StateMachine.currentState = states.PLAY;
        else
            StateMachine.currentState = states.STOP;
    }

    public static states getCurrentState(){
        return StateMachine.currentState;
    }

}
