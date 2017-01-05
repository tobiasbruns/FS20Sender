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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * created: 05.01.2017
 *
 * @author Tobias Bruns
 */
@RestController
@RequestMapping("/")
public class SenderController {

	@Autowired
	private FS20SenderService service;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public RootResponse root() {
		return new RootResponse(service.getFirmwareVersion());
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(method = RequestMethod.POST)
	public void sendRequest(@RequestBody FS20Request request) {
		FS20Result result = service.executeRequest(request);
		if (result.getResultCode().isError()) throw new CommandErrorException(result.getResultCode());
	}
}
