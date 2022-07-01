package com.file.service;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {

	List<String> uploadFiles(List<MultipartFile> multipartFiles);
	Path downloadFiles(String filename) throws FileNotFoundException;
	
}
