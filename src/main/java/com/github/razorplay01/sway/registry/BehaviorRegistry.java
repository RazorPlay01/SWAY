package com.github.razorplay01.sway.registry;

import com.github.razorplay01.sway.api.behavior.BehaviorKey;
import com.github.razorplay01.sway.api.behavior.SwayBehavior;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class BehaviorRegistry {
	private static final Map<BehaviorKey, SwayBehavior> REGISTRY = new ConcurrentHashMap<>();

	private BehaviorRegistry() {}

	public static void register(BehaviorKey key, SwayBehavior behavior) {
		if (REGISTRY.containsKey(key)) {
			throw new IllegalArgumentException("Behavior already registered: " + key);
		}
		REGISTRY.put(key, behavior);
	}

	public static Optional<SwayBehavior> get(BehaviorKey key) {
		return Optional.ofNullable(REGISTRY.get(key));
	}

	public static SwayBehavior getOrThrow(BehaviorKey key) {
		SwayBehavior b = REGISTRY.get(key);
		if (b == null) throw new IllegalArgumentException("Unknown behavior: " + key);
		return b;
	}

	public static boolean exists(BehaviorKey key) {
		return REGISTRY.containsKey(key);
	}

	public static Map<BehaviorKey, SwayBehavior> getAll() {
		return Collections.unmodifiableMap(REGISTRY);
	}
}
