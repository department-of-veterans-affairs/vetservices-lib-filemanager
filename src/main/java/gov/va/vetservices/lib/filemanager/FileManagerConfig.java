package gov.va.vetservices.lib.filemanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration for FileManager
 *
 * @author aburkholder
 */
@Configuration
@ComponentScan(basePackages = { "gov.va.vetservices.lib.filemanager.*" }, excludeFilters = @Filter(Configuration.class))
public class FileManagerConfig {

	FileManagerConfig() {
		super();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
