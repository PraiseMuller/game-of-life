package core.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private Map<String, Integer> uniformLocation;
    private int shaderProgramId, vertexShaderId, fragmentShaderId;

    public ShaderProgram(){
        this.uniformLocation = new HashMap<>();
        this.shaderProgramId = glCreateProgram();
        if (this.shaderProgramId == 0)
            throw new RuntimeException("Failed to create Shader program.");
    }

    public void createVertexShader(String shaderSource){
        this.vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        compileShader("Vertex_Shader", this.vertexShaderId, shaderSource);
    }
    public void createFragmentShader(String shaderSource){
        this.fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        compileShader("Fragment_Shader", this.fragmentShaderId, shaderSource);
    }
    private void compileShader(String shaderName, int shaderId, String shaderSource){
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        int success = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int length = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Failed to compile "+shaderName+" Shader: \n" + glGetShaderInfoLog(shaderId, length) + "\n" + shaderSource);
        }
    }
    public void link(){
        glAttachShader(this.shaderProgramId, this.vertexShaderId);
        glAttachShader(this.shaderProgramId, this.fragmentShaderId);
        glLinkProgram(this.shaderProgramId);

        int success = glGetProgrami(this.shaderProgramId, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int length = glGetShaderi(this.shaderProgramId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Shader linking failed: \n" + glGetProgramInfoLog(this.shaderProgramId, length));
        }

        if (this.vertexShaderId != 0) {
            glDetachShader(this.shaderProgramId, this.vertexShaderId);
        }

        if (this.fragmentShaderId != 0) {
            glDetachShader(this.shaderProgramId, this.fragmentShaderId);
        }

        glValidateProgram(this.shaderProgramId);
        if (glGetProgrami(this.shaderProgramId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning: Validating Shader code: " + glGetProgramInfoLog(this.shaderProgramId, 1024));
        }
    }
    public void bind(){
        glUseProgram(this.shaderProgramId);
    }
    public void unbind(){
        glUseProgram(0);
    }
    public void dispose(){
        unbind();
        if(this.shaderProgramId!= 0){
            glDeleteProgram(this.shaderProgramId);
        }
    }

    public void createUniform(String uniformName){
        int uniformLocation = glGetUniformLocation(this.shaderProgramId, uniformName);

        if(uniformLocation < 0){
            throw new RuntimeException("Could not find uniform: " + uniformName);
        }

        this.uniformLocation.put(uniformName, uniformLocation);
    }

    public void uploadMat4fUniform(String uniformName, Matrix4f value){
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer matBuffer = stack.mallocFloat(16);
            value.get(matBuffer);
            glUniformMatrix4fv(this.uniformLocation.get(uniformName), false, matBuffer);
        }
    }

    public void uploadVec4fUniform(String uniformName, Vector4f vec4){
        glUniform4f(this.uniformLocation.get(uniformName), vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3fUniform(String uniformName, Vector3f vec3){
        glUniform3f(this.uniformLocation.get(uniformName), vec3.x, vec3.y, vec3.z);
    }

    public void uploadIntUniform(String uniformName, int value) {
        glUniform1i(this.uniformLocation.get(uniformName), value);
    }

    public void uploadFloatUniform(String uniformName, float value) {
        glUniform1f(this.uniformLocation.get(uniformName), value);
    }

}
