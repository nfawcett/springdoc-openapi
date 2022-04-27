/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package test.org.springdoc.ui.app31;

import org.junit.jupiter.api.Test;
import test.org.springdoc.ui.AbstractSpringDocTest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = { "server.forward-headers-strategy=framework" })
public class SpringDocBehindProxyTest extends AbstractSpringDocTest {

	private static final String X_FORWARD_PREFIX = "/path/prefix";

	@SpringBootApplication
	static class SpringDocTestApp {}

	@Test
	public void shouldServeSwaggerUIAtDefaultPath() {
		webTestClient.get().uri("/webjars/swagger-ui/index.html").exchange()
				.expectStatus().isOk();
	}

	@Test
	public void shouldReturnCorrectInitializerJS() throws Exception {
		webTestClient
				.get().uri("/webjars/swagger-ui/swagger-initializer.js")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.consumeWith(response -> {
							String actualContent = response.getResponseBody();
							assertNotNull(actualContent);
							assertTrue(actualContent.contains("window.ui"));
							assertTrue(actualContent.contains("\"configUrl\" : \"/v3/api-docs/swagger-config\","));
							// TODO: what should be returned
							//assertTrue(actualContent.contains("\"configUrl\" : \"/path/prefix/v3/api-docs/swagger-config\","));
						}
				);
	}

	@Test
	public void shouldCalculateOauthRedirectBehindProxy() throws Exception {
		webTestClient
				.get().uri("/v3/api-docs/swagger-config")
				.header("X-Forwarded-Proto", "https")
				.header("X-Forwarded-Host", "proxy-host")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isOk().expectBody()
				.jsonPath("$.oauth2RedirectUrl").isEqualTo("https://proxy-host/path/prefix/swagger-ui/oauth2-redirect.html");
	}

	@Test
	public void shouldCalculateUrlsBehindProxy() throws Exception {
		webTestClient
				.get().uri("/v3/api-docs/swagger-config")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isOk().expectBody()
				.jsonPath("$.configUrl").isEqualTo("/path/prefix/v3/api-docs/swagger-config")
				.jsonPath("$.url").isEqualTo("/path/prefix/v3/api-docs");
	}
}
