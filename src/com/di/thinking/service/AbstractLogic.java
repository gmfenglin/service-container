package com.di.thinking.service;

import java.util.function.Consumer;

public abstract class AbstractLogic implements ServiceLogic {
	private ServiceContainer serviceContainer = new ServiceContainer();

	@Override
	public void data(Consumer<Internner> provider, Consumer<Internner> consumer) {

		serviceContainer.data(provider, consumer);

	}

	public void setLog(Consumer<String> logger) {
		serviceContainer.setLog(logger);
	}

	@Override
	public void job() {
		serviceContainer.job(packageJob());

	}

	protected abstract Consumer<ServiceContainer> packageJob();

	@Override
	public <R> R exec(String resultName) {
		return serviceContainer.exec(resultName);
	}

}