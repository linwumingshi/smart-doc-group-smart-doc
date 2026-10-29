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
package io.github.smartdoc.util;

import io.github.smartdoc.utils.JavaClassValidateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the dynamic JSON array support of {@link JavaClassValidateUtil}.
 *
 * <p>
 * The element type of {@code JSONArray} from fastjson 1.x/2.x, Gson and Jackson is
 * unknown at compile time. smart-doc must render it with array semantics, otherwise the
 * array is expanded as a POJO and incorrectly documented as an empty object.
 * </p>
 *
 * @author smart-doc
 */
public class JavaClassValidateUtilTest {

	/**
	 * Dynamic JSON arrays of all supported JSON libraries should be recognized.
	 */
	@Test
	public void testIsJsonArrayType() {
		assertTrue(JavaClassValidateUtil.isJsonArrayType("com.alibaba.fastjson.JSONArray"));
		assertTrue(JavaClassValidateUtil.isJsonArrayType("com.alibaba.fastjson2.JSONArray"));
		assertTrue(JavaClassValidateUtil.isJsonArrayType("com.google.gson.JsonArray"));
		assertTrue(JavaClassValidateUtil.isJsonArrayType("com.fasterxml.jackson.databind.node.ArrayNode"));
	}

	/**
	 * A user defined class sharing the same simple name must not be treated as a dynamic
	 * JSON array.
	 */
	@Test
	public void testIsJsonArrayTypeWithCustomClass() {
		assertFalse(JavaClassValidateUtil.isJsonArrayType("com.example.JSONArray"));
		assertFalse(JavaClassValidateUtil.isJsonArrayType("java.util.List"));
		assertFalse(JavaClassValidateUtil.isJsonArrayType("com.alibaba.fastjson2.JSONObject"));
	}

	/**
	 * Dynamic JSON objects of all supported JSON libraries should be recognized.
	 */
	@Test
	public void testIsJsonObjectType() {
		assertTrue(JavaClassValidateUtil.isJsonObjectType("com.alibaba.fastjson.JSONObject"));
		assertTrue(JavaClassValidateUtil.isJsonObjectType("com.alibaba.fastjson2.JSONObject"));
		assertTrue(JavaClassValidateUtil.isJsonObjectType("com.google.gson.JsonObject"));
		assertTrue(JavaClassValidateUtil.isJsonObjectType("com.fasterxml.jackson.databind.node.ObjectNode"));
	}

	/**
	 * A user defined class sharing the same simple name must not be treated as a dynamic
	 * JSON object.
	 */
	@Test
	public void testIsJsonObjectTypeWithCustomClass() {
		assertFalse(JavaClassValidateUtil.isJsonObjectType("com.example.JSONObject"));
		assertFalse(JavaClassValidateUtil.isJsonObjectType("java.util.Map"));
		assertFalse(JavaClassValidateUtil.isJsonObjectType("com.fasterxml.jackson.databind.node.ArrayNode"));
	}

	/**
	 * A dynamic JSON array must not reuse the Collection branch, which relies on generic
	 * parameters and degenerates to an empty collection because JSONArray has none.
	 */
	@Test
	public void testJsonArrayIsNotCollection() {
		assertFalse(JavaClassValidateUtil.isCollection("com.alibaba.fastjson2.JSONArray"));
		assertFalse(JavaClassValidateUtil.isCollection("com.google.gson.JsonArray"));
	}

}
