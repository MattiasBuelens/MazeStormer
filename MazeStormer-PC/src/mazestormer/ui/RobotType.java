package mazestormer.ui;

import java.util.HashSet;
import java.util.Set;
import be.kuleuven.cs.som.annotate.*;

/**
 * An enumeration containing the different robot types.
 * This contains: DEFAULT, PHYSICAL, VIRTUAL
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public enum RobotType{
	DEFAULT("---"), PHYSICAL("Physical"), VIRTUAL("Virtual");
	
	/**
     * Initialize this new robot type with the given robot type name.
     * 
     * @param	robotTypeName
     * 			The robot type name for this new robot type.
     * @post 	The robot type name for this new robot type is 
     * 			equal to the robot type name.
     *          | new.getRobotTypeName().equals(robotTypeName)
     * @throws	IllegalArgumentException
     * 			The given robot type name may not refer the null reference.
     * 			| robotTypeName == null
     */
    private RobotType(String robotTypeName) throws IllegalArgumentException{
    	if(robotTypeName == null)
    		throw new IllegalArgumentException("The given robot type name may not refer the null reference.");
    	this.robotTypeName = robotTypeName;
    }
    
    /**
     * Returns the robot type name of this robot type.
     */
    @Basic @Immutable
    public String getRobotTypeName(){
    	return this.robotTypeName;
    }
    
    /**
     * Variable storing the robot type name for this robot type.
     */
    private final String robotTypeName;
    
    /**
     * Checks if the given robot type is valid.
     * 
     * @param 	robotType
     * 			The robot type that has to be checked.
     * @return	True if and only if the given robot type
     * 			doesn't refer the null reference.
     * 			| result == (robotType != null)
     */
    public static boolean isValidRobotType(RobotType robotType){
    	return robotType != null;
    }
    
    /**
     * Checks if the given value corresponds to an existing robot type.
     * 
     * @param 	value
     * 			The value that has to be checked.
     * @return	True if and only if there exist
     * 			a not null referring robot type with
     * 			the given value as its name.
     * 			| result == there exists a m in getRobotTypes()
     * 			|			for which m.getRobotTypeName().equals(value)
     */
    public static boolean correspondsToExistingRobotType(String value){
    	for(RobotType rt : getAllRobotTypes()){
    		if(rt.getRobotTypeName().equals(value))
    			return true;
    	}
    	return false;
	}
    
    /**
     * Returns the robot type which robot type name
     * corresponds to the given request.
     * 
     * @param 	request
     * 			The request for which the robot type has to be returned.
     * @return	If the given request doesn't correspond to an existing
     * 			robot type, the DEFAULT robot type type is returned.
     * 			| if(!correspondsToExistingRobotType(request))
     * 			|	then result == DEFAULT
     * @return	If the given request corresponds to an existing
     * 			robot type, that robot type is returned.
     * 			| for each rt in getAllRobotTypes() :
     * 			|	if(rt.getRobotTypeName().equals(request))
     * 			|		then result == rt
     */
    public static RobotType getCorrespondingRobotType(String request){
    	for(RobotType rt : getAllRobotTypes()){
    		if(rt.getRobotTypeName().equals(request))
    			return rt;
    	}
    	return DEFAULT;
    }
    
    /**
     * Returns a collection with all the valid robot types.
     * 
     * @return	Returns a collection which contains all the valid robot types once.
     * 			| result.size() == RobotType.values().length &&
     * 			| for each rt in result :
     * 			| 	RobotType.isValidMotor(rt) == true
     */
    @Immutable
    public static Set<RobotType> getAllRobotTypes(){
    	Set<RobotType> temp = new HashSet<RobotType>();
    	for(int i=0; i <getNbOfRobotTypes(); i++)
    		temp.add(RobotType.values()[i]);
    	return temp;
    }
    
    /**
     * Returns an array with the name of all the valid robot types.
     * 
     * @return	Returns a collection which contains all the valid robot types once.
     * 			| result.length == RobotType.values().length &&
     * 			| for each i from 0 to getNbOfRobotTypes()-1 :
     * 			|	result[i].equals(RobotType.values()[i].getRobotTypeName())
     * 
     */
    public static String[] getAllRobotTypeNames(){
    	String[] temp = new String[getNbOfRobotTypes()];
    	for(int i=0; i <getNbOfRobotTypes(); i++)
    		temp[i] = (RobotType.values()[i].getRobotTypeName());
    	return temp;
    }
    
    /**
     * Returns the number of different valid robot types.
     * 
     * @return	Returns the number of different valid robot types.
     * 			| result == RobotType.values().length
     */
    @Immutable
    public static int getNbOfRobotTypes(){
    	return RobotType.values().length;
    }
}
