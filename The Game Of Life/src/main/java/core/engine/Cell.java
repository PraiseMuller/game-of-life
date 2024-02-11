package core.engine;

import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Cell {
    private final float size;
    private final float offset = 1f;  // Little spaces between the cells
    private Vector2f gridPosition;
    private float lifetime = 0;

    private final float[] vertices = {
            -0.5f,  0.5f, -1.0f,
             0.5f,  0.5f, -1.0f,
             0.5f, -0.5f, -1.0f,
            -0.5f, -0.5f, -1.0f,
    };
    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };
    private final int vao, ebo, vbo;

    private boolean isAlive;

    public Cell(Vector2f pos, int size){
        this.size = size;
        this.gridPosition = pos.mul(this.size + offset) .add(new Vector2f(this.size / 2));

        int rnum = (int) (Math.random() * 100);
        this.isAlive = (rnum % 5 == 0);

        //Initialize buffers and all that jazz
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        FloatBuffer fb = MemoryUtil.memAllocFloat(this.vertices.length);
        fb.put(this.vertices).flip();
        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        IntBuffer ib = MemoryUtil.memAllocInt(this.indices.length);
        ib.put(this.indices).flip();
        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        memFree(fb);
        memFree(ib);
    }

    public Cell copy(Cell from){
        this.gridPosition = new Vector2f(from.gridPosition.x, from.gridPosition.y);
        this.isAlive = from.isAlive;
        return this;
    }

    public void draw(){
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void dispose(){
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);
    }

    public void update(){
        if(lifetime > 0)
            lifetime -= 0.5f;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public void setAlive(boolean alive){
        this.isAlive = alive;
        if(isAlive) lifetime = 10;
    }

    public Vector4f getColor(){
        return isAlive ? new Vector4f(1) : new Vector4f(0,0,0,  1);
//        if(isAlive){
//            return new Vector4f(1);
//        } else if (!isAlive && lifetime > 0) {
//            return new Vector4f(0.8f,0.8f,0.8f,  1);
//        }else{
//            return new Vector4f(0,0,0,  1);
//        }
    }

    public Vector2f getGridPosition(){
        //this.gridPosition = pos.mul(this.size + offset) .add(new Vector2f(this.size / 2));
        Vector2f pos = new Vector2f(this.gridPosition);
        pos.sub(new Vector2f(this.size / 2)).div(this.size + offset);
        return pos;
    }

    public Matrix4f getMM(){

        Vector3f pos3f = new Vector3f(gridPosition.x, gridPosition.y, 0.0f);
        Matrix4f modelMatrix = new Matrix4f();

        modelMatrix.identity();
        modelMatrix.translate(pos3f);
        modelMatrix.scale(this.size);

        return modelMatrix;
    }
}
