/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(
			extensions = {
				@Extension(properties = {
					@ExtensionProperty(name = "x-api-id", value = "1"),
					@ExtensionProperty(name = "x-summary", value = "Feeds the Validator with configuration items")
				})
			},
			title = "Gateway Rules Manager", 
			version = "1.0.0", 
			description = "Feeds the Validator with configuration items",
			termsOfService = "${docs.info.termsOfService}", 
			contact = @Contact(name = "${docs.info.contact.name}", url = "${docs.info.contact.url}", email = "${docs.info.contact.mail}")),
	servers = {
		@Server(
			description = "Gateway Rules Manager Development URL",
			url = "http://localhost:8011",
			extensions = {
				@Extension(properties = {
					@ExtensionProperty(name = "x-sandbox", parseValue = true, value = "true")
				})
			}
		)
	})
public class OpenApiCFG {

  public OpenApiCFG() {
  }
 
}
