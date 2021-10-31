package software.plusminus.s3.service;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.s3.config.S3Autoconfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = S3Autoconfig.class)
@ActiveProfiles("test")
public class S3FileServiceIntegrationTest {

    private static final byte[] DATA = "some data".getBytes(Charset.defaultCharset());
    private static final String FOLDER = "my/path";

    @Autowired
    private S3FileService s3FileService;

    @After
    public void after() {
        s3FileService.getAllFilePaths(FOLDER)
                .forEach(s3FileService::remove);
    }

    @Test
    public void download_ReadsFileFromS3Bucket() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath = FOLDER + '/' + UUID.randomUUID().toString();
        s3FileService.upload(filepath, inputStream);
        //when
        String downloaded = IOUtils.toString(s3FileService.download(filepath));
        //then
        assertEquals(new String(DATA, Charset.defaultCharset()), downloaded);
    }

    @Test
    public void upload_CreatesFileInS3Bucket() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath = FOLDER + '/' + UUID.randomUUID().toString();
        //when
        s3FileService.upload(filepath, inputStream);
        //then
        String downloaded = IOUtils.toString(s3FileService.download(filepath));
        assertEquals(new String(DATA, Charset.defaultCharset()), downloaded);
    }

    @Test
    public void upload_ReturnsCorrectFilepath() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath = FOLDER + '/' + UUID.randomUUID().toString();
        //when
        URL url = s3FileService.upload(filepath, inputStream);
        //then
        assertEquals("https://s3.eu-central-1.amazonaws.com/plusminus-test/" + filepath,
                url.toString());
    }

    @Test
    public void upload_UpdatesFileInS3Bucket() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath = FOLDER + '/' + UUID.randomUUID().toString();
        s3FileService.upload(filepath, inputStream);
        //when
        String updatedData = "some updated data";
        s3FileService.upload(filepath, new ByteArrayInputStream(updatedData.getBytes(Charset.defaultCharset())));
        //then
        String downloaded = IOUtils.toString(s3FileService.download(filepath));
        assertEquals(updatedData, downloaded);
    }

    @Test
    public void remove_RemovesFileFromS3Bucket() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath = FOLDER + '/' + UUID.randomUUID().toString();
        s3FileService.upload(filepath, inputStream);
        //when
        s3FileService.remove(filepath);
        //then
        assertTrue(s3FileService.getAllFilePaths(FOLDER).isEmpty());
    }

    @Test
    public void getAllFilePaths_ReturnsFullFilepaths() {
        //given
        InputStream inputStream = new ByteArrayInputStream(DATA);
        String filepath1 = FOLDER + '/' + UUID.randomUUID().toString();
        String filepath2 = FOLDER + '/' + UUID.randomUUID().toString();
        String filepath3 = FOLDER + '/' + UUID.randomUUID().toString();
        s3FileService.upload(filepath1, inputStream);
        s3FileService.upload(filepath2, inputStream);
        s3FileService.upload(filepath3, inputStream);
        //when
        List<String> filepaths = s3FileService.getAllFilePaths(FOLDER);
        //then
        assertEquals(3, filepaths.size());
        assertTrue(filepaths.contains(filepath1));
        assertTrue(filepaths.contains(filepath2));
        assertTrue(filepaths.contains(filepath3));
    }
}