package mazestormer.util.state;

public interface State<M extends StateMachine<?, S>, S extends State<?, S>> {

	public void execute(M input);

}
