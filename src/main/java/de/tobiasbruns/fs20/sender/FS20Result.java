/*
 * Copyright 2016 the original author or authors.
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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
public class FS20Result {

	static enum ResultCode {
		SUCCESSFUL_PROCEEDED((short) 0x00, false), FIRMWARE((short) 0x01, false), UNKOWN_COMMAND_ID(
				(short) 0x02), WRONG_ORDER_LENGTH((short) 0x03), SENDING_CANCLED(
						(short) 0x04), NOTHING_TO_CANCCEL((short) 0x05, false), UNKOWN_ERROR((short) 0xFF);

		private static final Map<Short, ResultCode> CODE_MAP;
		static {
			CODE_MAP = Arrays.stream(ResultCode.values()).collect(Collectors.toMap(ResultCode::getCode, t -> t));
		}

		private final short code;
		private final boolean error;

		private ResultCode(short code) {
			this.code = code;
			this.error = true;
		}

		private ResultCode(short code, boolean error) {
			this.code = code;
			this.error = error;
		}

		public short getCode() {
			return code;
		}

		public boolean isError() {
			return error;
		}

		public static ResultCode byCode(short code) {
			return CODE_MAP.getOrDefault(code, UNKOWN_ERROR);
		}
	}

	private final ResultCode resultCode;
	private final short payload;

	public FS20Result(ResultCode resultCode, short payload) {
		super();
		this.resultCode = resultCode;
		this.payload = payload;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public short getPayload() {
		return payload;
	}

}
