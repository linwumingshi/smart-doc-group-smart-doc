/*
 * Copyright (C) 2018-2026 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.smartdoc.generation;

import io.github.smartdoc.extension.dict.DictionaryValuesResolver;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resolver that counts how many times the error code dictionary is built.
 * <p>
 * smart-doc instantiates the configured resolver once per dictionary build, so the
 * instance counter is a probe for the number of dictionary builds without touching the
 * production code.
 *
 * @author smart-doc
 */
public class CountingErrorCodeResolver implements DictionaryValuesResolver {

	private static final AtomicInteger INSTANCES = new AtomicInteger();

	/**
	 * Counts the instantiation.
	 */
	public CountingErrorCodeResolver() {
		INSTANCES.incrementAndGet();
	}

	/**
	 * Returns the number of instantiations since the last {@link #reset()}.
	 * @return instantiation count
	 */
	static int instances() {
		return INSTANCES.get();
	}

	/**
	 * Clears the counter.
	 */
	static void reset() {
		INSTANCES.set(0);
	}

}
