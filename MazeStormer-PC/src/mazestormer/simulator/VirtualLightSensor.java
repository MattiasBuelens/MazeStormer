package mazestormer.simulator;

import java.awt.geom.Rectangle2D;

import lejos.robotics.LampLightDetector;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;

public class VirtualLightSensor implements LampLightDetector{
	
	public VirtualLightSensor(Maze maze, PoseProvider poseProvider){
		this.maze = maze;
		this.poseProvider = poseProvider;
	}
	
	private PoseProvider getPoseProvider(){
		return this.poseProvider;
	}
	
	private PoseProvider poseProvider;
	
	private Maze getMaze(){
		return this.maze;
	}
	
	private Maze maze;
	
	@Override
	public int getLightValue(){
		return 0;
	}
	
	public static final int BROWN_VALUE = 410;
	public static final int WHITE_VALUE = 450;
	public static final int HALF_LINE_THICKNESS = 1;

	@Override
	public int getNormalizedLightValue(){
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());
		Tile tile = getMaze().getTileAt(pose.getLocation());
		for(Orientation orientation : tile.getOpenSides()){
			if(getRectangle(tile, orientation).contains(pose.getLocation()))	
				return WHITE_VALUE;
		}
		return BROWN_VALUE;
	}
	
	private Rectangle2D getRectangle(Tile tile, Orientation orientation){
		if(orientation == Orientation.NORTH)
			return new Rectangle2D.Float(tile.getX()-HALF_LINE_THICKNESS, tile.getY()+getMaze().getTileSize()+HALF_LINE_THICKNESS, getMaze().getTileSize()+2*HALF_LINE_THICKNESS, 2*HALF_LINE_THICKNESS);
		if(orientation == Orientation.SOUTH)
			return new Rectangle2D.Float(tile.getX()-HALF_LINE_THICKNESS, tile.getY()+HALF_LINE_THICKNESS, getMaze().getTileSize()+2*HALF_LINE_THICKNESS, 2*HALF_LINE_THICKNESS);
		if(orientation == Orientation.EAST)
			return new Rectangle2D.Float(tile.getX()+getMaze().getTileSize()-HALF_LINE_THICKNESS, tile.getY()+getMaze().getTileSize()+HALF_LINE_THICKNESS, 2*HALF_LINE_THICKNESS, getMaze().getTileSize()+2*HALF_LINE_THICKNESS);
		if(orientation == Orientation.WEST)
			return new Rectangle2D.Float(tile.getX()-HALF_LINE_THICKNESS, tile.getY()+getMaze().getTileSize()+HALF_LINE_THICKNESS, 2*HALF_LINE_THICKNESS, getMaze().getTileSize()+2*HALF_LINE_THICKNESS);
		return null;
	}

	@Override
	public int getHigh(){
		return 0;
	}

	@Override
	public int getLow(){
		return 0;
	}

	@Override
	public void setFloodlight(boolean floodlight){
		
	}

	@Override
	public boolean isFloodlightOn(){
		return true;
	}

	@Override
	public int getFloodlight(){
		return 0;
	}

	@Override
	public boolean setFloodlight(int color){
		return false;
	}

}
