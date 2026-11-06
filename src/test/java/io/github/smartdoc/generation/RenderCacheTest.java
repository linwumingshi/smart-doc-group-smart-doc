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

import io.github.smartdoc.constants.DocLanguage;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiErrorCodeDictionary;
import io.github.smartdoc.model.RevisionLog;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that one render builds the error code dictionary only once.
 *
 * @author smart-doc
 */
public class RenderCacheTest {

	private static final Path FIXTURE = Paths.get("src/test/resources/fixture-src").toAbsolutePath();

	/**
	 * The word renderer builds the error code document and the dictionary document from
	 * one template, so the error code dictionary must be built once, not once per
	 * document.
	 * @param outDir temporary output directory
	 */
	@Test
	public void wordRenderBuildsErrorCodesOnce(@TempDir Path outDir) {
		CountingErrorCodeResolver.reset();
		ApiConfig config = newConfig(outDir);
		new DocGenerator(config, newProjectBuilder()).generate(Collections.singletonList(RenderFormat.WORD));
		assertEquals(1, CountingErrorCodeResolver.instances());
	}

	/**
	 * All formats of one run share one template, so the error code dictionary must be
	 * built once for the whole run instead of once per format.
	 * @param outDir temporary output directory
	 */
	@Test
	public void formatsOfOneRunShareOneDictionary(@TempDir Path outDir) {
		CountingErrorCodeResolver.reset();
		ApiConfig config = newConfig(outDir);
		new DocGenerator(config, newProjectBuilder())
			.generate(Arrays.asList(RenderFormat.HTML, RenderFormat.WORD, RenderFormat.JMETER));
		assertEquals(1, CountingErrorCodeResolver.instances());
	}

	/**
	 * The cache belongs to one template instance, so a second run must build the
	 * dictionary again instead of reusing a stale list.
	 * @param outDir temporary output directory
	 */
	@Test
	public void separateRunsBuildErrorCodesAgain(@TempDir Path outDir) {
		CountingErrorCodeResolver.reset();
		ApiConfig config = newConfig(outDir);
		DocGenerator generator = new DocGenerator(config, newProjectBuilder());
		generator.generate(Collections.singletonList(RenderFormat.WORD));
		generator.generate(Collections.singletonList(RenderFormat.WORD));
		assertEquals(2, CountingErrorCodeResolver.instances());
	}

	private ApiConfig newConfig(Path outDir) {
		ApiConfig config = new ApiConfig();
		config.setProjectName("fixture");
		config.setServerUrl("http://127.0.0.1:8080");
		config.setOutPath(outDir.toAbsolutePath().toString());
		config.setBaseDir(FIXTURE.toString());
		config.setCodePath(FIXTURE.toString());
		config.setFramework("spring");
		config.setAllInOne(true);
		config.setCoverOld(true);
		config.setRandomMock(false);
		config.setCreateDebugPage(false);
		config.setLanguage(DocLanguage.ENGLISH);
		config.setClassLoader(RenderCacheTest.class.getClassLoader());
		config.setErrorCodeDictionaries(
				Collections.singletonList(new ApiErrorCodeDictionary().setEnumClass(SampleErrorCode.class)
					.setValuesResolverClass(CountingErrorCodeResolver.class.getName())));
		config.setRevisionLogs(Collections.singletonList(RevisionLog.builder()
			.setRevisionTime("2024-01-01 00:00:00")
			.setAuthor("@fixture")
			.setVersion("v1.0.0")
			.setRemarks("fixture")
			.setStatus("auto")));
		return config;
	}

	private JavaProjectBuilder newProjectBuilder() {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		javaProjectBuilder.setEncoding("UTF-8");
		javaProjectBuilder.addSourceTree(FIXTURE.toFile());
		return javaProjectBuilder;
	}

	/**
	 * Enum used to satisfy the enum class requirement of the error code dictionary. The
	 * resolving is delegated to {@link CountingErrorCodeResolver}.
	 */
	private enum SampleErrorCode {

		OK

	}

}
