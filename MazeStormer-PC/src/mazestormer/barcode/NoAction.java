package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.player.Player;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class NoAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);

		return new ImmediateFuture<Void>(null);
	}
}
