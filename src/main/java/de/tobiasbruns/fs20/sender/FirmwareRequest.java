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

/**
 * created: 04.01.2017
 *
 * @author Tobias Bruns
 */
public class FirmwareRequest implements FS20Request {

	private static final short BYTE_COUNT = 0x01;
	private static final short REQUEST_ID = 0xF0;

	@Override
	public byte[] toBytes() {
		byte[] data = new byte[11];
		data[0] = 0x01;
		data[1] = (byte) BYTE_COUNT;
		data[2] = (byte) REQUEST_ID;
		return data;
	}

}
