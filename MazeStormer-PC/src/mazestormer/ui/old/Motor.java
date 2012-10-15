package mazestormer.ui.old;

import java.util.HashSet;
import java.util.Set;
import be.kuleuven.cs.som.annotate.*;

/**
 * An enumeration containing the different motors.
 * This contains: A,B & C
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public enum Motor{
	A("A"),B("B"),C("C");
	
	/**
     * Initialize this new motor with the given motor name.
     * 
     * @param	motornr 
     * 			The motor name for this new motor.
     * @post 	The motor name for this new motor is 
     * 			equal to the given motor.
     *          | new.getMotorName().equals(motorname)
     */
    private Motor(String motorname){
    	this.motorname = motorname;
    }
    
    /**
     * Returns the motor name of this motor.
     */
    @Basic @Immutable
    public String getMotorName(){
    	return this.motorname;
    }
    
    /**
     * Variable storing the motor name for this motor.
     */
    private final String motorname;
    
    /**
     * Checks if the given motor is valid.
     * 
     * @param 	motor
     * 			The motor that has to be checked.
     * @return	True if and only if the given motor
     * 			doesn't refer the null reference.
     * 			| result == (motor != null)
     */
    public static boolean isValidMotor(Motor motor){
    	return motor != null;
    }
    
    /**
     * Checks if the given value corresponds to an existing motor.
     * 
     * @param 	value
     * 			The value that has to be checked.
     * @return	True if and only if there exist
     * 			a not null referring motor with
     * 			the given value as its name.
     * 			| result == there exists a m in getAllMotors()
     * 			|			for which m.getMotorName().equals(value)
     */
    public static boolean correspondsToExistingMotor(String value){
    	for(Motor m : getAllMotors()){
    		if(m.getMotorName().equals(value))
    			return true;
    	}
    	return false;
	}
    
    /**
     * Returns a collection with all the valid motors.
     * 
     * @return	Returns a collection which contains all the valid motors once.
     * 			| result.size() == Motor.values().length &&
     * 			| for each m in result :
     * 			| 	Motor.isValidMotor(m) == true
     */
    @Immutable
    public static Set<Motor> getAllMotors(){
    	Set<Motor> temp = new HashSet<Motor>();
    	for(int i=0; i <getNbOfMotors(); i++)
    		temp.add(Motor.values()[i]);
    	return temp;
    }
    
    /**
     * Returns the number of different valid motors.
     * 
     * @return	Returns the number of different valid motors.
     * 			| result == Motor.values().length
     */
    @Immutable
    public static int getNbOfMotors(){
    	return Motor.values().length;
    }
}
