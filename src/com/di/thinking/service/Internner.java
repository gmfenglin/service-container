package com.di.thinking.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Internner {
	private Map<String, Function<ServiceContainer, Object>> dataProviderContainer = new HashMap<>();
	private Map<String, Predicate<ServiceContainer>> dataProviderConditionContainer = new HashMap<>();
	private Map<String, Consumer<ServiceContainer>> dataConsumerContainer = new HashMap<>();
	private Map<String, Predicate<ServiceContainer>> dataConsumerConditionContainer = new HashMap<>();
	private Consumer<ServiceContainer> job = null;

	public Consumer<ServiceContainer> getJob() {
		return job;
	}

	public boolean isNullJob() {
		return job == null;
	}

	public Map<String, Function<ServiceContainer, Object>> getDataProviderContainer() {
		return dataProviderContainer;
	}

	public Map<String, Predicate<ServiceContainer>> getDataProviderConditionContainer() {
		return dataProviderConditionContainer;
	}

	public Map<String, Consumer<ServiceContainer>> getDataConsumerContainer() {
		return dataConsumerContainer;
	}

	public Map<String, Predicate<ServiceContainer>> getDataConsumerConditionContainer() {
		return dataConsumerConditionContainer;
	}

	public void setJob(Consumer<ServiceContainer> job) {
		this.job = job;
	}

	public Map<String, Function<ServiceContainer, Object>> getDataProviderContiner() {
		return dataProviderContainer;
	}

	public Map<String, Consumer<ServiceContainer>> getDataConsumerContiner() {
		return dataConsumerContainer;
	}

	public void provider(String name, Function<ServiceContainer, Object> fun, Predicate<ServiceContainer> predicate) {
		if (dataProviderContainer.containsKey(name)) {
			throw new RuntimeException(name + "provider data is exists.");
		}
		dataProviderConditionContainer.put(name, predicate);
		dataProviderContainer.put(name, fun);
	}

	public void provider(String name, Function<ServiceContainer, Object> fun) {
		if (dataProviderContainer.containsKey(name)) {
			throw new RuntimeException(name + "provider data is exists.");
		}
		dataProviderConditionContainer.put(name, co -> true);
		dataProviderContainer.put(name, fun);
	}

	public void consumer(String name, Consumer<ServiceContainer> fun, Predicate<ServiceContainer> predicate) {
		if (dataConsumerContainer.containsKey(name)) {
			throw new RuntimeException(name + "container data is exists.");
		}
		dataConsumerConditionContainer.put(name, predicate);
		dataConsumerContainer.put(name, fun);
	}

	public void consumer(String name, Consumer<ServiceContainer> fun) {
		if (dataConsumerContainer.containsKey(name)) {
			throw new RuntimeException(name + "container data is exists.");
		}
		dataConsumerConditionContainer.put(name, co -> true);
		dataConsumerContainer.put(name, fun);
	}

	public void clear() {
		dataProviderContainer.clear();
		dataConsumerContainer.clear();
		dataProviderConditionContainer.clear();
		dataConsumerConditionContainer.clear();
		this.job = null;
	}

	boolean isConsumerNull(String name) {
		return dataConsumerContainer.get(name) == null;
	}

	boolean isProviderNull(String name) {
		return dataProviderContainer.get(name) == null;
	}

}