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

import javax.usb.UsbDevice;
import javax.usb.UsbException;

import org.junit.Ignore;
import org.junit.Test;

import de.tobiasbruns.fs20.sender.FS20SenderService;
import de.tobiasbruns.fs20.sender.UsbService;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
@Ignore("Can only run with the Sender pluged in")
public class UsbFunctionalTest {

	private UsbService usbService = new UsbService();

	@Test
	public void findUsbSender() throws SecurityException, UsbException {
		UsbDevice device = getFS20Sender();
		assertThat(device).isNotNull();
	}

	@Test
	public void getFirmware() {
		UsbDevice device = getFS20Sender();

		byte[] data = new byte[11];
		data[0] = 0x01;
		data[1] = 0x01;
		data[2] = (byte) 0xF0; // read firmware

		byte[] result = usbService.sendAndRecv(device, data);
		verifyFirmewareRequestResult(result);
	}

	private void verifyFirmewareRequestResult(byte[] result) {
		assertThat(result[0]).isEqualTo((byte) 0x02);
		assertThat(result[1]).isEqualTo((byte) 0x03);
		assertThat(result[2]).isEqualTo((byte) 0xA0);
		assertThat(result[3]).isEqualTo((byte) 0x01);
	}

	@Test
	public void sendSwitchCommand() {
		byte[] data = new byte[11];
		data[0] = 0x01;
		data[1] = 0x06;
		data[2] = (byte) 0xF1; // send once
		data[3] = 0x00; // HC1
		data[4] = 0x00; // HC2
		data[5] = 0x00; // adress
		data[6] = 0x12; // switch

		byte[] result = usbService.sendAndRecv(getFS20Sender(), data);
		verifySwitchRequestResult(result);
	}

	private void verifySwitchRequestResult(byte[] result) {
		assertThat(result[0]).isEqualTo((byte) 0x02);
		assertThat(result[1]).isEqualTo((byte) 0x03);
		assertThat(result[2]).isEqualTo((byte) 0xA0);
		assertThat(result[3]).isEqualTo((byte) 0x00);
	}

	private UsbDevice getFS20Sender() {
		return usbService.findDevice(FS20SenderService.VENDOR_ID, FS20SenderService.PRODUCT_ID);
	}

}
