package com.file.service;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

	public static final String DIRECTORY = System.getProperty("user.home") + "/Downloads/uploads";

	@Override
	public List<String> uploadFiles(List<MultipartFile> multipartFiles) {

		List<String> filenames = new ArrayList<>();

		for (MultipartFile file : multipartFiles) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename());
			Path fileStorage = get(DIRECTORY, filename).toAbsolutePath().normalize();
			try {
				copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			filenames.add(filename);
		}
		return filenames;
	}

	@Override
	public Path downloadFiles(String filename) throws FileNotFoundException {
		Path filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);

		if (!Files.exists(filePath)) {
			throw new FileNotFoundException(filename + " was not found on the server");
		}
		return filePath;
	}
}
