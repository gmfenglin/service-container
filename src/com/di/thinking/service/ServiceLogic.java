package com.di.thinking.service;

import java.util.function.Consumer;

public interface ServiceLogic {
	void setLog(Consumer<String> logger);

	void data(Consumer<Internner> provider, Consumer<Internner> consumer);

	void job();

	<R> R exec(String resultName);

}