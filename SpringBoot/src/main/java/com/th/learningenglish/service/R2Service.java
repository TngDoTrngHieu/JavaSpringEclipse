package com.th.learningenglish.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class R2Service {

	private final S3Client s3;

	@Value("${r2.bucket}")
	private String bucket;

	@Value("${r2.account-id}")
	private String accountId;

	public R2Service(S3Client s3) {
		this.s3 = s3;
	}

	public String upload(MultipartFile file) throws IOException {

		String original = file.getOriginalFilename();

		if (original == null) {
			original = "audio.webm";
		}

		String fileName = original.replaceAll("\\s+", "_") // space → _
				.replaceAll("[^a-zA-Z0-9._-]", ""); // bỏ ký tự lạ

		String key = "audio/" + UUID.randomUUID() + "-" + fileName;

		s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).build(),
				software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

		return "https://" + accountId + ".r2.cloudflarestorage.com/" + bucket + "/" + key;
	}
}