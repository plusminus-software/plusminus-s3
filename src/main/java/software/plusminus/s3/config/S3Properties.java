package software.plusminus.s3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("s3")
public class S3Properties {
    
    private String bucketName;
    
}
