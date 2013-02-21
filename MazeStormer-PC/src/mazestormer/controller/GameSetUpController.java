package mazestormer.controller;

public class GameSetUpController extends SubController implements IGameSetUpController {

	public GameSetUpController(MainController mainController) {
		super(mainController);
	}

	@Override
	public void createGame() {
		// TODO Auto-generated method stub
		onJoin();
	}

	@Override
	public void joinGame(String gameID) {
		// TODO Auto-generated method stub
		onJoin();
	}

	@Override
	public String[] refreshLobby() {
		// TODO Auto-generated method stub
		String[] lobby = {"one", "two"};
		return lobby;
	}

	@Override
	public void leaveGame() {
		// TODO Auto-generated method stub
		onLeave();
	}
	
	private void onJoin() {
		postState(GameSetUpEvent.EventType.JOINED);
	}
	
	private void onLeave() {
		postState(GameSetUpEvent.EventType.LEFT);
	}
	
	private void onDisconnect() {
		postState(GameSetUpEvent.EventType.DISCONNECTED);
	}
	
	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}
}
