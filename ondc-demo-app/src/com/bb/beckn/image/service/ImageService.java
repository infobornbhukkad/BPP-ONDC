// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.image.service;

import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.bb.beckn.image.dao.ImageDao;
import com.bb.beckn.repository.ItemRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.repository.VendorRepository;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.VendorObj;


@Service
@Transactional
public class ImageService
{
	@Autowired
	private ImageDao dao;
	
	@Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
	@Autowired
	ItemRepository mItemRepository;
    @Autowired
	KiranaRepository mKiranaRepository;
    @Autowired
    VendorRepository mVendorRepository;
    
	public String saveImageToDatabase(String imageName) throws Exception {

		String path = "E:/Software/wget-1.21.2-win64/download/Files/" + imageName;
		if (!Files.exists(Paths.get(path)) || Files.isDirectory(Paths.get(path))) {
			throw new RuntimeException("image not found " + path);
		}

		InputStream in = new FileInputStream(path);

		byte[] iamgeBytes = StreamUtils.copyToByteArray(in);
		String base64String = Base64.encodeBase64String(iamgeBytes);

		String imageId = UUID.randomUUID().toString();
		/*ImageEntity entity = new ImageEntity();
		entity.setImageId(imageId);
		entity.setImageName(imageName);
		entity.setImageBytes(base64String);
		entity.setCreatedOn(LocalDateTime.now());

		this.dao.saveImage(entity);*/

		return imageId;
	}

public Resource getImageAsResource(String imageId , String imagename , String imagefield) {
		
		byte[] imageBytes = null;
		
		if(!imagefield.endsWith("fdetail")) {
			
		   List<ItemObj> itemObjopt= mItemRepository.findByImagesearch(Long.parseLong(imageId));
		   
			/*
			 * if (imagefield.contains("symbol")) { imageBytes =
			 * Base64.decodeBase64(itemObjopt.get(0).getItem_image_1()); }
			 */
   	        if (imagefield.equalsIgnoreCase("Item_image_1_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_1());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_2_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_2());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_3_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_3());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_4_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_4());
	   		}
		}else if(imagefield.endsWith("kdetail")) {
    	   
    	   List<ItemDetailsKirana> ItemDetailsObjopt= mItemRepositoryKirana.findByImagesearch(Long.parseLong(imageId));
			/*
			 * if (imagefield.contains("symbol")) { imageBytes =
			 * Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_1()); }
			 */
   	       if (imagefield.equalsIgnoreCase("Item_image_1_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_1());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_2_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_2());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_3_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_3());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_4_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_4());
	   		}
		}else if(imagefield.endsWith("symbol_name_k")) {
			
			List<KiranaObj> ItemDetailsObjopt= mKiranaRepository.findByImagesearch(Long.parseLong(imageId), imagename);
			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getSymbol());
			
		}else if(imagefield.endsWith("symbol_name_f")) {
			
			List<VendorObj> itemObjopt= mVendorRepository.findByImagesearch(Long.parseLong(imageId), imagename);
			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getSymbol());
		}
		
    	//ImageEntity entity = this.dao.getImageEntity(imageId);
		
		
		return new ByteArrayResource(imageBytes);
	}

   public byte[] getImageAsBytes(String imageId , String imagename, String imagefield) {
	byte[] imageBytes = null;
	    if(imagefield.endsWith("fdetail")) {
		
			List<ItemObj> itemObjopt= mItemRepository.findByImagesearch(Long.parseLong(imageId));
			   
			/*
			 * if (imagefield.contains("symbol")) { imageBytes =
			 * Base64.decodeBase64(itemObjopt.get(0).getItem_image_1()); }
			 */
    	    if (imagefield.equalsIgnoreCase("Item_image_1_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_1());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_2_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_2());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_3_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_3());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_4_fdetail")) {
	   			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getItem_image_4());
	   		}
	   		
			
		}else if(imagefield.endsWith("kdetail")) {
    	   
    	   List<ItemDetailsKirana> ItemDetailsObjopt= mItemRepositoryKirana.findByImagesearch(Long.parseLong(imageId));
			/*
			 * if (imagefield.contains("symbol")) { imageBytes =
			 * Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_1()); }
			 */
    	   if (imagefield.equalsIgnoreCase("Item_image_1_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_1());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_2_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_2());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_3_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_3());
	   		}
	   		if (imagefield.equalsIgnoreCase("Item_image_4_kdetail")) {
	   			imageBytes = Base64.decodeBase64(ItemDetailsObjopt.get(0).getItem_image_4());
	   		}
		}else if(imagefield.endsWith("symbol_name_k")) {
			
			 List<KiranaObj> kiranaObjopt = this.mKiranaRepository.findByImagesearch(Long.valueOf(Long.parseLong(imageId)), imagename);
		     imageBytes = Base64.decodeBase64(((KiranaObj)kiranaObjopt.get(0)).getSymbol());
			
		}else if(imagefield.endsWith("symbol_name_f")) {
			
			List<VendorObj> itemObjopt= mVendorRepository.findByImagesearch(Long.parseLong(imageId), imagename);
			imageBytes = Base64.decodeBase64(itemObjopt.get(0).getSymbol());
		}
	
	//ImageEntity entity = this.dao.getImageEntity(imageId);
	//imageBytes = Base64.decodeBase64(kiranaObjopt.get(0).getSymbol());
	return imageBytes;
  }
}
