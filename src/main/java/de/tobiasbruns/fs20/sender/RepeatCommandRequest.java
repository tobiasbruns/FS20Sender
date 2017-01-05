/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tobiasbruns.fs20.sender;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * created: 04.01.2017
 *
 * @author Tobias Bruns
 */
@JsonTypeName("repeatedCommand")
public class RepeatCommandRequest implements FS20Request {

	private static final short BYTE_COUNT = 0x07;
	private static final short REQUEST_ID = 0xF2;

	private final short times;
	private final CommandRequest commandRequest;

	@JsonCreator
	public RepeatCommandRequest(@JsonProperty("times") short times, @JsonProperty("command") CommandRequest command) {
		this.times = times;
		this.commandRequest = command;
	}

	@Override
	public byte[] toBytes() {
		byte[] data = commandRequest.toBytes();
		data[0] = 0x01;
		data[1] = (byte) BYTE_COUNT;
		data[2] = (byte) REQUEST_ID;
		data[8] = (byte) times;
		return data;
	}

}
