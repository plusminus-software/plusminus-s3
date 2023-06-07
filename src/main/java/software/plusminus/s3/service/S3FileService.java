package software.plusminus.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.plusminus.fs.exception.FileException;
import software.plusminus.fs.service.FileService;
import software.plusminus.s3.config.S3Properties;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3FileService implements FileService {

    public static final String URL_PATTERN = "https://s3.%s.amazonaws.com/%s/%s";

    private AmazonS3 amazonS3;
    private TransferManager transferManager;
    private S3Properties s3Properties;

    @Override
    public InputStream download(String filepath) {
        return amazonS3.getObject(s3Properties.getBucketName(), filepath).getObjectContent();
    }

    @Override
    public URL upload(String filepath, InputStream data) {
        ObjectMetadata metadata = new ObjectMetadata();
        String contentType = URLConnection.guessContentTypeFromName(filepath);
        if (contentType != null) {
            metadata.setContentType(contentType);
        }
        PutObjectRequest request = new PutObjectRequest(s3Properties.getBucketName(),
                filepath, data, metadata);
        try {
            transferManager.upload(request.withCannedAcl(CannedAccessControlList.PublicRead))
                    .waitForUploadResult();
        } catch (InterruptedException e) {
            log.error("Exception during file upload", e);
            Thread.currentThread().interrupt();
        }
        try {
            return new URL(String.format(URL_PATTERN, amazonS3.getRegion(),
                    s3Properties.getBucketName(), filepath));
        } catch (MalformedURLException e) {
            throw new FileException(e);
        }
    }

    @Override
    public void remove(String filepath) {
        DeleteObjectRequest request = new DeleteObjectRequest(s3Properties.getBucketName(), filepath);
        amazonS3.deleteObject(request);
    }

    @Override
    public List<String> getAllFilePaths(String folderPath) {
        ObjectListing objectListing = amazonS3.listObjects(s3Properties.getBucketName(), folderPath);
        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
}
