package mazestormer.simulator;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;

public class VirtualRangeScanner implements RangeScanner{
	
	public VirtualRangeScanner(Maze maze, PoseProvider poseProvider){
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
	public RangeReadings getRangeValues(){
		RangeReadings r = new RangeReadings(this.angles.length);
		for(int i=0; i<this.angles.length; i++){
			r.add(new RangeReading(this.angles[i], generateRange(this.angles[i])));
		}
		return r;
	}
	
	private float generateRange(float angle){
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());
		
	    Line l = new  Line(pose.getX(), pose.getY(), pose.getX() + 254f
	    	        * (float) Math.cos(Math.toRadians(pose.getHeading())), pose.getY() + 254f
	    	        * (float) Math.sin(Math.toRadians(pose.getHeading())));
	    Line rl = null;

	    Line[] lines = (Line[]) getMaze().getLines().values().toArray();
	    for(int i=0; i<lines.length; i++){
	      Point p = lines[i].intersectsAt(l);
	      if(p == null) continue; // Does not intersect
	      Line tl = new Line(pose.getX(), pose.getY(), p.x, p.y);
	      // If the range line intersects more than one map line
	      // then take the shortest distance.
	      if (rl == null || tl.length() < rl.length()) rl = tl;
	    }
	    return (rl == null ? -1 : rl.length());
	}

	@Override
	public void setAngles(float[] angles){
		this.angles = angles;
	}
	
	private float[] angles;

	@Override
	public RangeFinder getRangeFinder(){
		return null;
	}
}
