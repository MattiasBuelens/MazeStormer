package mazestormer.controller;

import mazestormer.player.Game;
import mazestormer.rabbitmq.Config;
import mazestormer.rabbitmq.ConnectionMode;
import mazestormer.rabbitmq.MQChannel;

public class GameSetUpController extends SubController implements
		IGameSetUpController {

	private Game game;

	private final MQChannel channel;

	public GameSetUpController(MainController mainController) {
		super(mainController);
		this.channel = new MQChannel(ConnectionMode.LOCAL);
	}

	@Override
	public void createGame(String gameID) {
		this.game = new Game(gameID);
		this.game.addPlayer(getMainController().getPlayer());
		this.channel.addToGameList(this.game);
		this.channel.subscribeGameToJoin(this.game);
		onJoin();
	}

	@Override
	public void joinGame(String gameID) {
		this.channel.sendMessageTo("request", "race." + gameID + ".join");
		// TODO Auto-generated method stub
		onJoin();
	}

	@Override
	public String[] refreshLobby() {
		channel.sendMessageTo("race.replyTo_Brons", Config.GAME_LIST);
		// TODO Auto-generated method stub
		String[] lobby = { "one", "two" };
		return lobby;
	}

	@Override
	public void leaveGame() {
		// TODO Auto-generated method stub
		onLeave();
	}

	private boolean isReady() {
		// TODO cheating still possible
		if (getMainController().getPlayer().getRobot() == null) {
			return false;
		} else if (mazestormer.simulator.VirtualRobot.class
				.isInstance(getMainController().getPlayer().getRobot())
				&& getMainController().getSourceMaze().getNumberOfTiles() == 0) {
			return false;
		}
		return true;
	}

	private void onJoin() {
		if (isReady())
			postState(GameSetUpEvent.EventType.JOINED);
		else
			onNotReady();
	}

	private void onLeave() {
		postState(GameSetUpEvent.EventType.LEFT);
	}

	private void onDisconnect() {
		postState(GameSetUpEvent.EventType.DISCONNECTED);
	}

	private void onNotReady() {
		postState(GameSetUpEvent.EventType.NOT_READY);
	}

	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}
}
