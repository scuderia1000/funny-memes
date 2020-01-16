package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

@Service
public class DownloadServiceImpl implements DownloadService {

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

    @Override
    public byte[] downloadImage(String url) {
//        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
//        HttpEntity<Request>
        String fileName = url.substring(url.lastIndexOf("/"), url.length() - 1);



        try {
            ReadableByteChannel readChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOS = new FileOutputStream("/tmp/" + fileName);
            FileChannel writeChannel = fileOS.getChannel();
            writeChannel
                    .transferFrom(readChannel, 0, Long.MAX_VALUE);
            File file = new File("/tmp" + fileName);

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



        return new byte[0];
    }
}
