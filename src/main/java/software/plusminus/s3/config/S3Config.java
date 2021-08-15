package software.plusminus.s3.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("software.plusminus.s3")
public class S3Config {

    @ConditionalOnMissingBean(AmazonS3.class)
    @Bean
    public AmazonS3 amazonS3(AwsProperties properties) {
        AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(properties.getRegion())
                .build();
    }

    @ConditionalOnMissingBean(TransferManager.class)
    @Bean
    public TransferManager transferManager(AmazonS3 amazonS3) {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
    }
}
