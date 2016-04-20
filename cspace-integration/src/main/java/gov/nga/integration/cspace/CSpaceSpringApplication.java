package gov.nga.integration.cspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
public class CSpaceSpringApplication {

    public static void main(String[] args) {
    	// could initialize here if needed, but hmm...
        SpringApplication.run(CSpaceSpringApplication.class, args);
    }
    
}