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

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("command")
public class CommandRequest implements FS20Request {

	private short hauscode1;
	private short hauscode2;
	private short address;
	private short command;
	private short time;

	private static final short BYTE_COUNT = 0x06;
	private static final short REQUEST_ID = 0xF1;

	public CommandRequest() {

	}

	private CommandRequest(Builder builder) {
		this.hauscode1 = builder.hauscode1;
		this.hauscode2 = builder.hauscode2;
		this.address = builder.address;
		this.command = builder.command;
		this.time = builder.time;
	}

	public static Builder build() {
		return new Builder();
	}

	public short getHauscode1() {
		return hauscode1;
	}

	public short getHauscode2() {
		return hauscode2;
	}

	public short getAddress() {
		return address;
	}

	public short getCommand() {
		return command;
	}

	public short getTime() {
		return time;
	}

	@Override
	public byte[] toBytes() {
		byte[] data = new byte[11];

		data[0] = 0x01;
		data[1] = (byte) BYTE_COUNT;
		data[2] = (byte) REQUEST_ID;
		data[3] = (byte) getHauscode1();
		data[4] = (byte) getHauscode2();
		data[5] = (byte) getAddress();
		data[6] = (byte) getCommand();
		data[7] = (byte) getTime();

		return data;
	}

	public static class Builder {
		private short hauscode1;
		private short hauscode2;
		private short address;
		private short command;
		private short time;

		public Builder withHauscode(short hauscode1, short hauscode2) {
			this.hauscode1 = hauscode1;
			this.hauscode2 = hauscode2;
			return this;
		}

		public Builder withAddress(short address) {
			this.address = address;
			return this;
		}

		public Builder withCommand(short command) {
			this.command = command;
			return this;
		}

		public Builder withTime(short time) {
			this.time = time;
			return this;
		}

		public CommandRequest get() {
			return new CommandRequest(this);
		}
	}

}
