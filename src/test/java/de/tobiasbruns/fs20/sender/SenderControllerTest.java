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
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.tobiasbruns.fs20.sender.CommandRequest;
import de.tobiasbruns.fs20.sender.ExceptionController;
import de.tobiasbruns.fs20.sender.FS20Request;
import de.tobiasbruns.fs20.sender.FS20Result;
import de.tobiasbruns.fs20.sender.FS20SenderService;
import de.tobiasbruns.fs20.sender.RepeatCommandRequest;
import de.tobiasbruns.fs20.sender.SenderController;
import de.tobiasbruns.fs20.sender.FS20Result.ResultCode;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
@RunWith(MockitoJUnitRunner.class)
public class SenderControllerTest {

	@InjectMocks
	private SenderController controller;
	@Mock
	private FS20SenderService service;
	@Captor
	private ArgumentCaptor<FS20Request> requestCaptor;

	private MockMvc mockMvc;

	@Before
	public void initTest() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ExceptionController()).build();

		when(service.executeRequest(any(FS20Request.class)))
				.thenReturn(new FS20Result(ResultCode.SUCCESSFUL_PROCEEDED, (short) 0));
	}

	@Test
	public void root() throws Exception {
		when(service.getFirmwareVersion()).thenReturn("2.1");

		mockMvc.perform(get("/"))//
				.andExpect(status().isOk())//
				.andExpect(jsonPath("$.firmwareVersion", is("2.1")));
	}

	@Test
	public void sendSwitchRequest() throws Exception {
		String request = TestUtils.loadTextFile("requests/SwitchCommandRequest.json");

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isNoContent());

		verify(service).executeRequest(requestCaptor.capture());
		assertThat(requestCaptor.getValue()).isInstanceOf(CommandRequest.class);

		CommandRequest comReq = (CommandRequest) requestCaptor.getValue();
		assertThat(comReq.getCommand()).isEqualTo((short) 18);
	}

	@Test
	public void sendRepeatedCommand() throws Exception {
		String request = TestUtils.loadTextFile("requests/RepeatedCommandRequest.json");

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isNoContent());

		verify(service).executeRequest(requestCaptor.capture());
		assertThat(requestCaptor.getValue()).isInstanceOf(RepeatCommandRequest.class);
	}

	@Test
	public void sendUnkownRequest() throws Exception {
		when(service.executeRequest(any(FS20Request.class))).thenReturn(errorResult());

		String request = TestUtils.loadTextFile("requests/SwitchCommandRequest.json");

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().is4xxClientError());

	}

	private FS20Result errorResult() {
		return new FS20Result(ResultCode.UNKOWN_COMMAND_ID, (short) 0);

	}
}
