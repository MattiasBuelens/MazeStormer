package mazestormer.physical;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lejos.pc.comm.NXTConnector;
import mazestormer.command.Command;
import mazestormer.remote.Communicator;
import mazestormer.remote.MessageListener;
import mazestormer.report.Report;
import mazestormer.report.ReportReader;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class PhysicalCommunicator extends Communicator<Command, Report<?>> {

	private NXTConnector connector;
	private final List<MessageListener<? super Report<?>>> listeners;

	private volatile AtomicInteger nextRequestId = new AtomicInteger();

	private static ThreadFactory factory = new ThreadFactoryBuilder()
			.setNameFormat("PhysicalCommunicator-%d").build();
	private final ExecutorService executor = Executors
			.newCachedThreadPool(factory);

	public PhysicalCommunicator(NXTConnector connector) {
		super(connector.getInputStream(), connector.getOutputStream(),
				new ReportReader());
		this.connector = connector;
		this.listeners = new CopyOnWriteArrayList<MessageListener<? super Report<?>>>();
	}

	public int nextRequestId() {
		return nextRequestId.incrementAndGet();
	}

	@Override
	public void terminate() throws IOException {
		super.terminate();
		if (!executor.isShutdown()) {
			executor.shutdown();
		}
		if (connector != null) {
			connector.close();
			connector = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Listeners will only start receiving messages after the current message is
	 * processed by all currently registered listeners.
	 * </p>
	 */
	@Override
	public void addListener(MessageListener<? super Report<?>> listener) {
		super.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Listeners will only stop receiving messages after the current message is
	 * processed by all currently registered listeners.
	 * </p>
	 */
	@Override
	public void removeListener(MessageListener<? super Report<?>> listener) {
		super.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Changes to the list of registered listeners are only applied after the
	 * current message is processed by all currently registered listeners.
	 * </p>
	 */
	@Override
	public void trigger(final Report<?> report) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				PhysicalCommunicator.super.trigger(report);
			}
		});
	}

	@Override
	protected List<MessageListener<? super Report<?>>> getListeners() {
		return listeners;
	}

}
