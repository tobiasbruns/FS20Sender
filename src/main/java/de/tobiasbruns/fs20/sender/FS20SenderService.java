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

import javax.usb.UsbDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tobiasbruns.fs20.sender.FS20Result.ResultCode;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
@Service
public class FS20SenderService {

	static final short VENDOR_ID = 0x18ef;
	static final short PRODUCT_ID = (short) 0xe015;

	@Autowired
	private UsbService usbService;

	public FS20Result executeRequest(FS20Request request) {
		UsbDevice device = findSender();
		byte[] resultData = usbService.sendAndRecv(device, request.toBytes());
		return new FS20Result(ResultCode.byCode(resultData[3]), resultData[4]);
	}

	public String getFirmwareVersion() {
		FS20Result result = executeRequest(new FirmwareRequest());
		char[] versionArray = Integer.toHexString(result.getPayload() & 0xffff).toCharArray();
		return versionArray[0] + (versionArray.length > 1 ? "." + versionArray[1] : "");
	}

	public void checkResult(FS20Result result) {
		if (result.getResultCode().isError()) throw new RuntimeException("Error Received: " + result.getResultCode());
	}

	private UsbDevice findSender() {
		return usbService.findDevice(VENDOR_ID, PRODUCT_ID);
	}

}
