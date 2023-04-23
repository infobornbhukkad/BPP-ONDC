package com.bb.beckn.search.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//import static com.seller.external.constant.ApplicationConstant.EXTERNAL_CONTEXT_ROOT;
//import static com.seller.external.constant.ApplicationConstant.IMAGE_ENDPOINT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bb.beckn.ConnectionUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.springframework.core.io.UrlResource;

//import com.seller.internal.service.FilesStorageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ResourceController {

	//@Autowired
	//private FilesStorageService storageService;
	private static final String IMAGE_FOLDER_PATH = "images";

	private final Path imagePath = Paths.get(IMAGE_FOLDER_PATH);
	
	
	@PostMapping(value =  "/public/image/{id}/{name}" )
	public ResponseEntity<Resource> getFileResource(@PathVariable String id, @PathVariable String name) throws IOException, SQLException {
		log.info("going to load image name {} & id {}", name, id);
		HttpHeaders headers = new HttpHeaders();
		Resource resource = this.load(id, name);

		if (resource != null) {
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		}
		 //return new ResponseEntity<>(resource, headers, HttpStatus.NOT_FOUND);
		return ResponseEntity.notFound().headers(headers).build();
	}
	public Resource load(String id, String filename) throws SQLException, IOException {
		try {
			Path file = this.imagePath.resolve(filename);
			
			System.out.println("file -- "+ file.toString());
			String temppath= "https://localhost:443/images"+ filename;
			Connection myConSruct = ConnectionUtil.getConnection();
	    	String mySql_vendor= "select * from bornbhukkad.bb_admin_panel_vendors_kirana where symbol_name='" +filename+"'";
	    	PreparedStatement myStatConSruct1 = myConSruct.prepareStatement(mySql_vendor);
	    	ResultSet myResultSetConSruct1 = myStatConSruct1.executeQuery();
	    	FileOutputStream fout = null;
	    	
	    	while (myResultSetConSruct1.next()) {
	    	     System.out.println("myResultSetConSruct1 2---"+myResultSetConSruct1.getString("symbol"));
	    	     byte barr[]= myResultSetConSruct1.getString("symbol").getBytes();
	    	     fout=new FileOutputStream(temppath);  
	    	     fout.write(barr);  
	    	           
	    	     fout.close(); 
	    	}
	    
			Resource resource = new UrlResource(fout.toString() );

			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			// throw new RuntimeException("Could not read the file!");
			return null;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}
}
