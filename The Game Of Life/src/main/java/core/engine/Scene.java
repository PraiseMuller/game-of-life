package core.engine;

import core.inputs.Input;
import core.utils.AssetPool;
import core.utils.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static core.utils.Constants.*;

public class Scene {
    private final ImGuiLayer imGuiLayer;
    private final ShaderProgram shaderProgram;
    private final Matrix4f projectionMatrix;
    private Cell[][] grid;
    private Cell[][] nextGen;
    private final int CELL_SIZE = 50;
    private final int gridWidth = WIN_WIDTH / CELL_SIZE;
    private final int gridHeight = WIN_HEIGHT / CELL_SIZE;
    private int generation = 0;

    public Scene(){
        imGuiLayer = new ImGuiLayer(Window.getWindow());
        imGuiLayer.initImGui();

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/fragment.glsl"));
        this.shaderProgram.link();
        this.shaderProgram.bind();
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createUniform("cellColor");
        this.shaderProgram.unbind();

        this.projectionMatrix = new Matrix4f().identity();
        this.projectionMatrix.ortho(0f, WIN_WIDTH, WIN_HEIGHT, 0, Z_NEAR, Z_FAR, false);

        this.grid = new Cell[gridWidth][gridHeight];
        this.reloadGrid();
    }

    public void reloadGrid(){
        for(int i = 0; i < gridWidth; i++){
            for(int j = 0; j < gridHeight; j++){
                this.grid[i][j] = new Cell(new Vector2f(i, j), CELL_SIZE);
            }
        }
    }

    public void updateInputs(float dt){
        Input.update(dt, this);

        //update cells if clicked
        if(Input.mouseLeftClicked()){
            Vector2f pos = Input.getMousePosition();
            int x = (int) (pos.x / CELL_SIZE);
            int y = (int) (pos.y / CELL_SIZE);

            grid[x][y].setAlive(!grid[x][y].isAlive());
        }
    }

    public void update(float dt){
        //THE LOGIC
        nextGen = newArr(this.grid);
        for(int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                //count neighbors
                Cell current_cell = this.grid[i][j];
                current_cell.update();
                int aliveNeighbors = countNeighbors(current_cell, i, j);

                //apply rules
                if(current_cell.isAlive()  &&  (aliveNeighbors < 2 || aliveNeighbors > 3)){
                    nextGen[i][j].setAlive(false);
                }
                else if(!current_cell.isAlive()  &&  aliveNeighbors == 3){
                    nextGen[i][j].setAlive(true);
                }
            }
        }

        this.grid = nextGen;
        this.generation++;
    }

    private int countNeighbors(Cell cell, int x, int y){
        int count = 0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){

                int col = (x + i + gridWidth) % gridWidth;
                int row = (y + j + gridHeight) % gridHeight;

                if(this.grid[col][row].isAlive() && this.grid[col][row] != cell){
                    count += 1;
                }
            }
        }
        return count;
    }

    private Cell[][] newArr(Cell[][] from){
        Cell[][] a = new Cell[from.length][from[0].length];
        for(int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                Vector2f pos = from[i][j].getGridPosition();
                a[i][j] = new Cell(new Vector2f(pos.x, pos.y), CELL_SIZE);
                a[i][j].setAlive(from[i][j].isAlive());

                //a[i][j].copy(from[i][j]);
            }
        }
        return a;
    }

    public void render(){
        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", this.projectionMatrix);

         for(int i = 0; i < gridWidth; i++){
             for(int j = 0; j < gridHeight; j++) {
                 Cell cell = this.grid[i][j];

                 this.shaderProgram.uploadMat4fUniform("modelMatrix", cell.getMM());
                 this.shaderProgram.uploadVec4fUniform("cellColor", cell.getColor());

                 cell.draw();
             }
         }

        this.shaderProgram.unbind();
    }

    public void renderGUI(float dt){
        imGuiLayer.update(dt, generation, this);
    }

    public void dispose(){
        this.shaderProgram.unbind();
        this.shaderProgram.dispose();
        this.imGuiLayer.destroyImGui();
        for(int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                grid[i][j].dispose();
            }
        }
    }

}
