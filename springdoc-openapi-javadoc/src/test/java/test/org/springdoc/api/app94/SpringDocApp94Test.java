package test.org.springdoc.api.app94;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.RandomStringUtils;
import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.ActuatorProvider;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.RepositoryRestResourceProvider;
import org.springdoc.core.SecurityOAuth2Provider;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.RouterFunctionProvider;
import test.org.springdoc.api.AbstractSpringDocTest;
import test.org.springdoc.api.app91.Greeting;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The type Spring doc app 94 test.
 */
@TestPropertySource(properties = "springdoc.default-produces-media-type=application/json")
public class SpringDocApp94Test extends AbstractSpringDocTest {

	/**
	 * The type Spring doc test app.
	 */
	@SpringBootApplication
	static class SpringDocTestApp implements ApplicationContextAware {

		/**
		 * The Application context.
		 */
		private ApplicationContext applicationContext;

		/**
		 * Greeting controller greeting controller.
		 *
		 * @return the greeting controller
		 */
		@Bean
		public GreetingController greetingController() {
			return new GreetingController();
		}

		/**
		 * Custom open api open api builder customizer.
		 *
		 * @return the open api builder customizer
		 */
		@Bean
		public OpenApiBuilderCustomizer customOpenAPI() {
			return openApiBuilder -> openApiBuilder.addMappings(Collections.singletonMap("greetingController", new GreetingController()));
		}

		/**
		 * Default test handler mapping request mapping handler mapping.
		 *
		 * @param greetingController the greeting controller 
		 * @return the request mapping handler mapping 
		 * @throws NoSuchMethodException the no such method exception
		 */
		@Bean
		public RequestMappingHandlerMapping defaultTestHandlerMapping(GreetingController greetingController) throws NoSuchMethodException {
			RequestMappingHandlerMapping result = new RequestMappingHandlerMapping();
			RequestMappingInfo requestMappingInfo =
					RequestMappingInfo.paths("/test").methods(RequestMethod.GET).produces(MediaType.APPLICATION_JSON_VALUE).build();

			result.setApplicationContext(this.applicationContext);
			result.registerMapping(requestMappingInfo, "greetingController", GreetingController.class.getDeclaredMethod("sayHello2"));
			//result.handlerme
			return result;
		}

		/**
		 * Open api resource open api web mvc resource.
		 *
		 * @param openAPIBuilderObjectFactory the open api builder object factory 
		 * @param requestBuilder the request builder 
		 * @param responseBuilder the response builder 
		 * @param operationParser the operation parser 
		 * @param operationCustomizers the operation customizers 
		 * @param requestMappingHandlerMapping the request mapping handler mapping 
		 * @param actuatorProvider the actuator provider 
		 * @param springDocConfigProperties the spring doc config properties 
		 * @param openApiCustomisers the open api customisers 
		 * @param springSecurityOAuth2Provider the spring security o auth 2 provider 
		 * @param routerFunctionProvider the router function provider 
		 * @param repositoryRestResourceProvider the repository rest resource provider 
		 * @return the open api web mvc resource
		 */
		@Bean(name = "openApiResource")
		public OpenApiWebMvcResource openApiResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder,
				OperationService operationParser,Optional<List<OperationCustomizer>> operationCustomizers,
				@Qualifier("defaultTestHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping,
				Optional<ActuatorProvider> actuatorProvider, SpringDocConfigProperties springDocConfigProperties,
				Optional<List<OpenApiCustomiser>> openApiCustomisers, Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
				Optional<RouterFunctionProvider> routerFunctionProvider, Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider) {
			return new OpenApiWebMvcResource(DEFAULT_GROUP_NAME, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, requestMappingHandlerMapping,
					actuatorProvider,operationCustomizers, openApiCustomisers, springDocConfigProperties, springSecurityOAuth2Provider,routerFunctionProvider, repositoryRestResourceProvider);
		}

		/**
		 * Sets application context.
		 *
		 * @param applicationContext the application context 
		 * @throws BeansException the beans exception
		 */
		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}
	}

	/**
	 * The type Greeting controller.
	 */
	@ResponseBody
	@Tag(name = "Demo", description = "The Demo API")
	public static class GreetingController {

		/**
		 * Say hello response entity.
		 *
		 * @return the response entity
		 */
		@GetMapping(produces = APPLICATION_JSON_VALUE)
		@Operation(summary = "This API will return a random greeting.")
		public ResponseEntity<Greeting> sayHello() {
			return ResponseEntity.ok(new Greeting(RandomStringUtils.randomAlphanumeric(10)));
		}

		/**
		 * Say hello 2 response entity.
		 *
		 * @return the response entity
		 */
		@GetMapping("/test")
		@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "item created"),
				@ApiResponse(responseCode = "400", description = "invalid input, object invalid"),
				@ApiResponse(responseCode = "409", description = "an existing item already exists") })
		public ResponseEntity<Greeting> sayHello2() {
			return ResponseEntity.ok(new Greeting(RandomStringUtils.randomAlphanumeric(10)));
		}

	}
}