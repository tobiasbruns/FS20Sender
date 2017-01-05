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

import java.util.List;
import java.util.Objects;

import javax.usb.UsbClaimException;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotOpenException;
import javax.usb.UsbPipe;

import org.springframework.stereotype.Service;

/**
 * created: 12.12.2016
 *
 * @author Tobias Bruns
 */
@Service
public class UsbService {

	public UsbDevice findDevice(short vendorId, short productId) {
		return findDevice(getRootHub(), vendorId, productId);
	}

	private UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
		for (UsbDevice device : getAttachedUsbDevices(hub)) {
			UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
			if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device, vendorId, productId);
				if (device != null) return device;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<UsbDevice> getAttachedUsbDevices(UsbHub hub) {
		return (List<UsbDevice>) hub.getAttachedUsbDevices();
	}

	private UsbHub getRootHub() {
		try {
			return UsbHostManager.getUsbServices().getRootUsbHub();
		} catch (SecurityException | UsbException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] sendAndRecv(UsbDevice device, byte[] data) {
		UsbConfiguration config = device.getActiveUsbConfiguration();
		UsbInterface iface = config.getUsbInterface((byte) 0);
		return sendAndRecvData(iface, data);
	}

	private byte[] sendAndRecvData(UsbInterface iface, byte[] data) {
		Objects.requireNonNull(iface, "UsbInterface must not be null");

		try {
			forceClaim(iface);
			try {
				UsbPipe pipe = iface.getUsbEndpoint((byte) 0x01).getUsbPipe();
				sendData(pipe, data);
				pipe = iface.getUsbEndpoint((byte) 0x81).getUsbPipe();
				return reciveData(pipe);
			} finally {
				iface.release();
			}
		} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
			throw new RuntimeException(e);
		}
	}

	public void forceClaim(UsbInterface iface)
			throws UsbClaimException, UsbNotActiveException, UsbDisconnectedException, UsbException {
		iface.claim(new UsbInterfacePolicy() {
			@Override
			public boolean forceClaim(UsbInterface usbInterface) {
				return true;
			}
		});
	}

	private void sendData(UsbPipe pipe, byte[] data) {
		syncSubmit(pipe, data);
	}

	private byte[] reciveData(UsbPipe pipe) {
		byte[] result = new byte[5];
		syncSubmit(pipe, result);
		return result;
	}

	private void syncSubmit(UsbPipe pipe, byte[] data) {
		try {
			pipe.open();
			pipe.syncSubmit(data);
		} catch (UsbNotActiveException | UsbNotOpenException | IllegalArgumentException | UsbDisconnectedException
				| UsbException e) {
			throw new RuntimeException(e);
		} finally {
			if (pipe.isOpen()) closePipe(pipe);
		}
	}

	private void closePipe(UsbPipe pipe) {
		try {
			pipe.close();
		} catch (UsbNotActiveException | UsbNotOpenException | UsbDisconnectedException | UsbException e) {
			throw new RuntimeException(e);
		}
	}

}
