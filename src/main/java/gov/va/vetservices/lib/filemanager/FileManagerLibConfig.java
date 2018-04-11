/**
 *
 */
package gov.va.vetservices.lib.filemanager;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import gov.va.ascent.framework.config.BaseYamlConfig;

/**
 * Library configuration - specifically, the messages that can be returned from FileManager.
 *
 * DO NOT OVERRIDE THESE MESSAGAES.
 *
 * @author aburkholder
 */
@Configuration
@ComponentScan(basePackages = "gov.va.vetservices.lib.filemanager.*")
public class FileManagerLibConfig extends BaseYamlConfig {

	/** The Constant MESSAGE_PROPERTIES */
	private static final String MESSAGE_PROPERTIES = "classpath:/filemanager-messages.yml";

	/**
	 * The local messages environment configuration.
	 */
	@Configuration
	@PropertySource(MESSAGE_PROPERTIES)
	static class DefaultEnvironment extends BaseYamlEnvironment {
	}
}
