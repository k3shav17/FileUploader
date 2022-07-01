package com.file.resource;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.file.service.FileProcessingService;

@RestController
@RequestMapping("/file")
public class FileResource {

	@Autowired
	private FileProcessingService fileProcessingService;

	@PostMapping("/upload")
	public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> multipartFiles)
			throws IOException {

		return ResponseEntity.ok().body(fileProcessingService.uploadFiles(multipartFiles));
	}

	@GetMapping("/download/{filename}")
	public ResponseEntity<Resource> downloadFiles(@PathVariable("filename") String filename) throws IOException {

		Path filePath = fileProcessingService.downloadFiles(filename);
		Resource resource = new UrlResource(filePath.toUri());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("File-name", filename);
		httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
				.headers(httpHeaders).body(resource);

	}

}
