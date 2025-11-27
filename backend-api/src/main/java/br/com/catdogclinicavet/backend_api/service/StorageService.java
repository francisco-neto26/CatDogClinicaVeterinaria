package br.com.catdogclinicavet.backend_api.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name:pet-photos}")
    private String bucketName;

    @Value("${minio.public-url}")
    private String publicUrl;

    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            setPublicPolicy(bucketName);

            String imageName = "bg-auth.jpg";
            try {
                minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(imageName).build());
            } catch (Exception e) {
                ClassPathResource imgFile = new ClassPathResource("static/" + imageName);
                if (imgFile.exists()) {
                    try (InputStream is = imgFile.getInputStream()) {
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(imageName)
                                        .stream(is, imgFile.contentLength(), -1)
                                        .contentType("image/jpeg")
                                        .build()
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPublicPolicy(String bucket) {
        try {
            String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                setPublicPolicy(bucketName);
            }

            String safeName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = UUID.randomUUID() + "_" + safeName;
            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return publicUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            // Logamos o erro mas não paramos o sistema, pois é apenas uma limpeza
            System.err.println("Erro ao tentar deletar arquivo do Minio: " + e.getMessage());
        }
    }
}