package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.command.CommandTools;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class NoAction implements IAction {

	@Override
	public Future<?> performAction(CommandTools player) {
		checkNotNull(player);

		return new ImmediateFuture<Void>(null);
	}
}
