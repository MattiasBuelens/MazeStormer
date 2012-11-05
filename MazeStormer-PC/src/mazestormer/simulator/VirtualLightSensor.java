package mazestormer.simulator;

import java.awt.Rectangle;

import lejos.geom.Line;
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
	
	public static final int BROWN_VALUE = 410;
	public static final int WHITE_VALUE = 450;
	public static final int HALF_LINE_THICKNESS = 1;

	@Override
	public int getLightValue(){
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());
		Tile tile = getMaze().getTileAt(getPoseProvider().getPose().getLocation());
		for(Orientation orientation : tile.getOpenSides()){
			Line l = getLine(tile, orientation);
			int dx = 0;
			int dy = 0;
			if(orientation == Orientation.NORTH || orientation == Orientation.SOUTH)
				dy = HALF_LINE_THICKNESS;
			if(orientation == Orientation.EAST || orientation == Orientation.WEST)
				dx = HALF_LINE_THICKNESS;
			if(l.getP1().getX()-dx <= pose.getX() && pose.getX() <= l.getP1().getX()+dx)
				if(l.getP1().getX() <= pose.getX() && pose.getX() <= l.getP2().getX())		
					if(l.getP1().getY()-dy <= pose.getY() && pose.getY() <= l.getP1().getY()+dy)		
						if(l.getP1().getY() <= pose.getY() && pose.getY() <= l.getP2().getY())	
							return WHITE_VALUE;
		}
		return BROWN_VALUE;
	}
	
	private Line getLine(Tile tile, Orientation orientation){
		if(orientation == Orientation.NORTH)
			return new Line(tile.getX(), tile.getY()+getMaze().getTileSize(), tile.getX()+getMaze().getTileSize(), tile.getY()+getMaze().getTileSize());
		if(orientation == Orientation.SOUTH)
			return new Line(tile.getX(), tile.getY(), tile.getX()+getMaze().getTileSize(), tile.getY());
		if(orientation == Orientation.EAST)
			return new Line(tile.getX()+getMaze().getTileSize(), tile.getY(), tile.getX()+getMaze().getTileSize(), tile.getY()+getMaze().getTileSize());
		if(orientation == Orientation.WEST)
			return new Line(tile.getX(), tile.getY(), tile.getX(), tile.getY()+getMaze().getTileSize());
		return null;
	}

	@Override
	public int getNormalizedLightValue() {
		// TODO Auto-generated method stub
		return 0;
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
