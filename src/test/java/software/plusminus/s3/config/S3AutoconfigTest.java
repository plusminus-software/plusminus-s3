package software.plusminus.s3.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = S3Autoconfig.class)
@ActiveProfiles("test")
public class S3AutoconfigTest {

    @Test
    public void testAutoConfiguration() {
    }
    
}