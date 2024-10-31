package edu.handong.csee.isel.cfg;

/**
 * Exception that is raised when a Json file does not follow the desired 
 * specification of <code>bytecode</code> json array.
 * <br></br>
 * This exception will be thrown by <code>JsonReader</code> when a read Json 
 * file uses an unsupported opcode in its <code>bytecode</code> json array.
 * 
 * @see JsonReader
 */
public class UnsupportedOpcodeException extends Exception {
    public UnsupportedOpcodeException(String message) {
        super(message);
    }
}
