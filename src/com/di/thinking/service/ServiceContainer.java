package com.di.thinking.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ServiceContainer {
	private Map<String, Object> container = new HashMap<>();

	private Internner internner = new Internner();
	private Consumer<String> log = (txt) -> {
	};

	ServiceContainer getContainer() {
		return this;
	}

	public void setLog(Consumer<String> log) {
		getContainer().log = log;
	}

	Function<ServiceContainer, Object> getProvider(String name) {
		Optional<Function<ServiceContainer, Object>> optionalFun = Optional
				.ofNullable(internner.getDataProviderContiner().get(name));
		if (!optionalFun.isPresent()) {
			throw new RuntimeException(name + "input is not exists.");
		}
		return optionalFun.get();
	}

	Consumer<ServiceContainer> getConsumer(String name) {
		Optional<Consumer<ServiceContainer>> optionalFun = Optional
				.ofNullable(internner.getDataConsumerContiner().get(name));
		if (!optionalFun.isPresent()) {
			throw new RuntimeException(name + "input is not exists.");
		}
		return optionalFun.get();
	}

	public ServiceContainer logic(String comment, String name, Supplier<Object> supplier) {
		logic(comment, name, supplier, () -> true);

		return this;
	}

	public ServiceContainer logic(String comment, String name, Supplier<Object> supplier, Supplier<Boolean> predicate) {
		if (predicate.get()) {
			if (log != null) {
				log.accept(comment);
			}
			if (container.containsKey(name)) {
				throw new RuntimeException(name + " is exists.");
			}
			container.put(name, supplier.get());
			if (getContainer().isNull(name)) {
				return this;
			}
			if (internner.isConsumerNull(name)) {
				return this;
			}
			if (internner.getDataConsumerConditionContainer().get(name).test(getContainer())) {
				internner.getDataConsumerContiner().get(name).accept(getContainer());
			}

		}

		return this;
	}

	public boolean isNull(String name) {
		Object o = container.get(name);
		if (o instanceof List) {
			return Optional.ofNullable(container.get(name)).isPresent() && ((List) o).size() == 0;
		}
		if (o instanceof Map) {
			return Optional.ofNullable(container.get(name)).isPresent() && ((Map) o).size() == 0;
		}
		if (o instanceof String) {
			return Optional.ofNullable(container.get(name)).isPresent() && ((String) o).trim().length() == 0;
		}
		return !Optional.ofNullable(container.get(name)).isPresent();
	}

	public <R> R getOrNull(String name) {
		Optional<R> rOptional = getOptional(name);
		if (rOptional.isPresent()) {
			return rOptional.get();
		} else {
			if (!internner.isProviderNull(name)) {
				if (internner.getDataProviderConditionContainer().get(name).test(getContainer())) {
					container.put(name, internner.getDataProviderContiner().get(name).apply(getContainer()));
					if (!(getContainer().isNull(name) || internner.isConsumerNull(name))) {
						if (internner.getDataConsumerConditionContainer().get(name).test(getContainer())) {
							internner.getDataConsumerContiner().get(name).accept(getContainer());
						}
					}
					Optional<R> optional = getOptional(name);
					if (optional.isPresent()) {
						R r = optional.get();
						if (r instanceof String) {
							String tmpStr = (String) r;
							if (tmpStr.trim().length() != 0) {
								return (R) tmpStr.trim();
							} 
						}
						return r;
					}
				}
			}
			return null;
		}
	}

	/**
	 * 获取值
	 */
	public <R> R get(String name) {
		Optional<R> rOptional = getOptional(name);
		if (rOptional.isPresent()) {
			R r = rOptional.get();
			if (r instanceof String) {
				String tmpStr = (String) r;
				if (tmpStr.trim().length() != 0) {
					return (R) tmpStr.trim();
				} else {
					throw new RuntimeException(name + " is empty.");
				}
			}
			return r;
		} else {
			if (!internner.isProviderNull(name)) {
				if (internner.getDataProviderConditionContainer().get(name).test(getContainer())) {
					container.put(name, internner.getDataProviderContiner().get(name).apply(getContainer()));
					if (!(getContainer().isNull(name) || internner.isConsumerNull(name))) {
						if (internner.getDataConsumerConditionContainer().get(name).test(getContainer())) {
							internner.getDataConsumerContiner().get(name).accept(getContainer());
						}
					}
					Optional<R> optional = getOptional(name);
					if (optional.isPresent()) {
						R r = optional.get();
						if (r instanceof String) {
							String tmpStr = (String) r;
							if (tmpStr.trim().length() != 0) {
								return (R) tmpStr.trim();
							} else {
								throw new RuntimeException(name + " is empty.");
							}
						}
						return r;
					}
				}
			}
			throw new RuntimeException(name + " is null.");
		}
	}

	<R> Optional<R> getOptional(String name) {
		R r = (R) container.get(name);
		Optional<R> rOptional = Optional.ofNullable(r);
		return rOptional;
	}

	void clear() {
		internner.clear();
		container.clear();
	}

	public ServiceContainer data(Consumer<Internner> provider, Consumer<Internner> consumer) {
		if (provider != null) {
			provider.accept(internner);
		}
		if (consumer != null) {
			consumer.accept(internner);
		}
		return this;
	}

	/**
	 * 业务逻辑
	 */
	public ServiceContainer job(Consumer<ServiceContainer> job) {
		internner.setJob(job);
		return this;
	}

	/**
	 * 业务逻辑
	 */
	public <R> R exec(String resultName) {
		if (internner.isNullJob()) {
			throw new RuntimeException("job is null.");
		}
		internner.getJob().accept(getContainer());
		R r = get(resultName);
		clear();
		return r;
	}

}
