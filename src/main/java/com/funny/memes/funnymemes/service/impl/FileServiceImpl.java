package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class FileServiceImpl implements FileService {

    private static final String DIR = "/tmp/";
//    private static final HttpHeaders headers = new HttpHeaders();
//
//    static {
//        final List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
//        acceptableMediaTypes.add(MediaType.IMAGE_JPEG);
//        acceptableMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//
//        headers.setAccept(acceptableMediaTypes);
//    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Value("${app.awsServices.bucketName}")
    private String amazonBucketName;

    @Override
    public byte[] downloadImage(String url) {
//        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
//        HttpEntity<Request>
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            try {
                ReadableByteChannel readChannel = Channels.newChannel(new URL(url).openStream());
                FileOutputStream fileOS = new FileOutputStream(DIR + fileName);
                FileChannel writeChannel = fileOS.getChannel();
                writeChannel
                        .transferFrom(readChannel, 0, Long.MAX_VALUE);
                uploadMediaToS3(fileName);
//            File file = new File("/tmp" + fileName);

//            URL imageUrl = new URL(url);
//            BufferedImage bufferedImage = ImageIO.read(imageUrl);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//            byteArrayOutputStream.flush();
//            MultipartFile multipartFile = new Mu(fileName,fileName,imageType,byteArrayOutputStream.toByteArray());
//            byteArrayOutputStream.close()

//            BufferedInputStream in = new BufferedInputStream(imageUrl.openStream());


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new byte[0];
    }

    @Override
    public void uploadMediaToS3(String fileName) {
        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(
                PutObjectRequest.builder()
                        .bucket(amazonBucketName)
                        .key(fileName)
                        .build(),
                AsyncRequestBody.fromFile(Paths.get(DIR + fileName))
        );
        future.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    System.out.println("my response: " + resp);
                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Lets the application shut down. Only close the client when you are completely done with it.
                s3AsyncClient.close();
            }
        });

        future.join();
    }
}
