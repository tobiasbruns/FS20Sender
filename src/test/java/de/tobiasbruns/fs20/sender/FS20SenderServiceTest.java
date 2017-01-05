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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.usb.UsbDevice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.tobiasbruns.fs20.sender.CommandRequest;
import de.tobiasbruns.fs20.sender.FS20Result;
import de.tobiasbruns.fs20.sender.FS20SenderService;
import de.tobiasbruns.fs20.sender.FirmwareRequest;
import de.tobiasbruns.fs20.sender.RepeatCommandRequest;
import de.tobiasbruns.fs20.sender.StopSendingRequest;
import de.tobiasbruns.fs20.sender.UsbService;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
@RunWith(MockitoJUnitRunner.class)
public class FS20SenderServiceTest {

	@InjectMocks
	private FS20SenderService service;

	@Mock
	private UsbService usbService;
	@Mock
	private UsbDevice usbDevice;
	@Captor
	private ArgumentCaptor<byte[]> dataCaptor;

	private static final byte HAUS_CODE_1 = 0x11;
	private static final byte HAUS_CODE_2 = 0x22;
	private static final byte ADDRESS = 0x33;
	private static final byte COMMAND = 0x44;
	private static final byte TIME = 0x55;

	@Before
	public void initTest() {
		when(usbService.findDevice(anyShort(), anyShort())).thenReturn(usbDevice);
		when(usbService.sendAndRecv(any(UsbDevice.class), any(byte[].class))).thenReturn(buildResultArray());
	}

	@Test
	public void sendRequest() {
		service.executeRequest(buildCommandRequest());

		verify(usbService).findDevice(FS20SenderService.VENDOR_ID, FS20SenderService.PRODUCT_ID);
		verify(usbService).sendAndRecv(same(usbDevice), dataCaptor.capture());

		assertThat(dataCaptor.getValue()).hasSize(11).startsWith((byte) 0x01, (byte) 0x06, (byte) 0xF1, HAUS_CODE_1,
				HAUS_CODE_2, ADDRESS, COMMAND, TIME);
	}

	@Test
	public void sendFirmwareRequest() {
		service.executeRequest(new FirmwareRequest());

		verify(usbService).sendAndRecv(same(usbDevice), dataCaptor.capture());

		assertThat(dataCaptor.getValue()).hasSize(11).startsWith((byte) 0x01, (byte) 0x01, (byte) 0xF0);
	}

	@Test
	public void sendRepeatedRequest() {
		service.executeRequest(new RepeatCommandRequest((short) 5, buildCommandRequest()));

		verify(usbService).findDevice(FS20SenderService.VENDOR_ID, FS20SenderService.PRODUCT_ID);
		verify(usbService).sendAndRecv(same(usbDevice), dataCaptor.capture());

		assertThat(dataCaptor.getValue()).hasSize(11).startsWith((byte) 0x01, (byte) 0x07, (byte) 0xF2, HAUS_CODE_1,
				HAUS_CODE_2, ADDRESS, COMMAND, TIME, (byte) 5);
	}

	@Test
	public void sendStopSendingRequest() {
		service.executeRequest(new StopSendingRequest());

		verify(usbService).sendAndRecv(same(usbDevice), dataCaptor.capture());

		assertThat(dataCaptor.getValue()).hasSize(11).startsWith((byte) 0x01, (byte) 0x01, (byte) 0xF3);
	}

	@Test
	public void commandSuccess() {
		FS20Result result = service.executeRequest(buildCommandRequest());

		assertThat(result).isNotNull();
		assertThat(result.getResultCode()).isEqualTo(FS20Result.ResultCode.SUCCESSFUL_PROCEEDED);
	}

	@Test
	public void getFirmwareVersion() {
		byte[] result = buildResultArray();
		result[4] = (byte) 0x11;
		when(usbService.sendAndRecv(any(UsbDevice.class), any(byte[].class))).thenReturn(result);

		String firmwareVersion = service.getFirmwareVersion();

		verify(usbService).sendAndRecv(same(usbDevice), dataCaptor.capture());
		assertThat(dataCaptor.getValue()).hasSize(11).startsWith((byte) 0x01, (byte) 0x01, (byte) 0xF0);

		assertThat(firmwareVersion).isEqualTo("1.1");
	}

	private CommandRequest buildCommandRequest() {
		return CommandRequest.build().withHauscode(HAUS_CODE_1, HAUS_CODE_2).withAddress(ADDRESS).withCommand(COMMAND)
				.withTime(TIME).get();

	}

	private byte[] buildResultArray() {
		byte[] result = new byte[5];
		result[0] = 0x02;
		result[1] = 0x03;
		result[2] = (byte) 0xA0;
		return result;
	}
}
