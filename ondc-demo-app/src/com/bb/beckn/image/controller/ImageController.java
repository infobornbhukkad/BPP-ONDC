// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.image.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.binary.Base64;
import com.bb.beckn.image.service.ImageService;
import org.springframework.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class ImageController
{
	//IMAGE_JPEG_VALUE
	@Autowired
	private ImageService imageService;
	@Autowired
	@Value("classpath:bornbhkkad.jpeg")
	private Resource resource;

	@GetMapping(value = "/image/download/{id}/{imagename}/{imagefield}" , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> getFileResource(@PathVariable String id ,@PathVariable String imagename , @PathVariable String imagefield) throws IOException {
		log.info("going to load image {}", id);
		HttpHeaders headers = new HttpHeaders();
		Resource resource = this.imageService.getImageAsResource(id,imagename, imagefield);

		if (resource != null) {
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		}
		return ResponseEntity.notFound().headers(headers).build();
	}

	@GetMapping(value = "/image/view/{id}/{imagename}/{imagefield}" , produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getFileBytes(@PathVariable String id ,@PathVariable String imagename , @PathVariable String imagefield) throws IOException {
		log.info("going to load image as bytes {}", id);
		return this.imageService.getImageAsBytes(id, imagename, imagefield);

	}
	@GetMapping(value = "/image/check/{imagename}" , produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getFileBytes1(@PathVariable String imagename ) throws IOException {
		
		byte[] imageBytes = null;
		log.info("going to load image as bytes {}", imagename);
		InputStream is = getClass().getResourceAsStream("/"+ resource.getFilename());
		
		byte[] iamgeBytes = StreamUtils.copyToByteArray(is);
		String base64String = Base64.encodeBase64String(iamgeBytes);
		
		imageBytes = Base64.decodeBase64(base64String);	
		
		return imageBytes;

	}

	@GetMapping(value = "/image/save/{name}")
	public String saveImage(@PathVariable String name) throws Exception {
		log.info("going to save image {}", name);
		String imageId = this.imageService.saveImageToDatabase(name);
		log.info("image saved with id {}", imageId);
		return imageId;
	}

    
    
    
}
