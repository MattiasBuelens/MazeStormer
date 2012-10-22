package mazestormer.robot;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;

public class CachedRemoteMotor extends RemoteMotor {

	private static final long cacheDuration = 100;
	private static final TimeUnit cacheTimeUnit = TimeUnit.MILLISECONDS;

	public CachedRemoteMotor(NXTCommand nxtCommand, int id) {
		super(nxtCommand, id);
	}

	private enum CacheKey {
		TachoCount, IsMoving;
	}

	private Cache<CacheKey, Object> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(cacheDuration, cacheTimeUnit).build();

	@Override
	public int getTachoCount() {
		try {
			return (Integer) cache.get(CacheKey.TachoCount,
					new Callable<Object>() {
						@Override
						public Object call() {
							return CachedRemoteMotor.super.getTachoCount();
						}
					});
		} catch (ExecutionException e) {
			return super.getTachoCount();
		}
	}

	@Override
	public boolean isMoving() {
		try {
			return (Boolean) cache.get(CacheKey.IsMoving,
					new Callable<Object>() {
						@Override
						public Object call() {
							return CachedRemoteMotor.super.isMoving();
						}
					});
		} catch (ExecutionException e) {
			return super.isMoving();
		}
	}

	@Override
	public void resetTachoCount() {
		// Clear cached value
		cache.invalidate(CacheKey.TachoCount);
		super.resetTachoCount();
	}
}
