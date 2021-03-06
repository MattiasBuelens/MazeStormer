package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IManualControlController extends EventSource {

	public void moveForward();

	public void moveBackward();

	public void rotateLeft();

	public void rotateRight();

	public void stop();

	public void travel(float distance);

	public void rotate(float angle);

	public IParametersController parameters();

	public IScanController scan();

}
