package com.bb.beckn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONException;

@SpringBootApplication
public class BecknMultiApplication
{  
	
	
    public static void main(final String[] args) throws Exception, JSONException {
        SpringApplication.run((Class)BecknMultiApplication.class, args);
        

	    //Map<String, String> address = reverseGeocode(latitude, longitude, apiKey);

	    //System.out.println("Reverse geocoded address: " + address);
	        
       // System.out.println("test --- " + ConnectionUtil.getConnection());
        
        
       /* String path = "E:/Software/wget-1.21.2-win64/download/food/"+"plain_paratha_thali.jpg";
		if (!Files.exists(Paths.get(path)) || Files.isDirectory(Paths.get(path))) {
			throw new RuntimeException("image not found " + path);
		}

		InputStream in = new FileInputStream(path);

		byte[] iamgeBytes = StreamUtils.copyToByteArray(in);
		String base64String = Base64.encodeBase64String(iamgeBytes);

				
		PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement
				("update bornbhukkad.bb_admin_panel_vendors_item_details set symbol=?, symbol_name=?, short_desc=?, long_desc=?, Item_image_1_name=?,"
						+ "Item_image_2_name=?, Item_image_3_name=?, Item_image_4_name=? , Item_image_1=?, Item_image_2=?, Item_image_3=?, Item_image_4=?"
						+ " where id=?");
		 
		 pstmt.setString(1, base64String);
		 pstmt.setString(2, "plain_paratha_thali.jpg");
		 pstmt.setString(3, "Paratha (4 pcs) + 2 sabji");
		 pstmt.setString(4, "Paratha (4 pcs) + 2 sabji");
		 pstmt.setString(5, "plain_paratha_thali.jpg");
		 pstmt.setString(6, "plain_paratha_thali.jpg");
		 pstmt.setString(7, "plain_paratha_thali.jpg");
		 pstmt.setString(8, "plain_paratha_thali.jpg");
		 pstmt.setString(9, base64String);
		 pstmt.setString(10, base64String);
		 pstmt.setString(11, base64String);
		 pstmt.setString(12, base64String);		 
		 pstmt.setString(13, "418");
         
         pstmt.execute();
         System.out.println("Record inserted......");*/
         
		/*PreparedStatement pstmt1 = ConnectionUtil.getConnection().prepareStatement("update bornbhukkad.bb_admin_panel_vendors_item_details_kirana set Item_image_2=?, Item_image_2_name=? where id=?");
		 
		 pstmt1.setString(1, base64String);
		 pstmt1.setString(2, "40253563-2_1-madhur-sugar-pure-hygienic-fine-grain-natural-sulphur-free.jpg");
         pstmt1.setString(3, "34");
         
         pstmt1.execute();
         System.out.println("Record inserted......");*/
         
		 /*PreparedStatement pstmt3 = ConnectionUtil.getConnection().prepareStatement("update bornbhukkad.bb_admin_panel_vendors_item_details_kirana set Item_image_3=?, Item_image_3_name=? where id=?");
		 
		 pstmt3.setString(1, base64String);
		 pstmt3.setString(2, "40253563-3_1-madhur-sugar-pure-hygienic-fine-grain-natural-sulphur-free.jpg");
         pstmt3.setString(3, "34");
         
         pstmt3.execute();
         System.out.println("Record inserted......");*/
         
		/*PreparedStatement pstmt4 = ConnectionUtil.getConnection().prepareStatement("update bornbhukkad.bb_admin_panel_vendors_item_details_kirana set Item_image_4=?, Item_image_4_name=? where id=?");
		 
		 pstmt4.setString(1, base64String);
		 pstmt4.setString(2, "40253563-4_1-madhur-sugar-pure-hygienic-fine-grain-natural-sulphur-free.jpg");
         pstmt4.setString(3, "34");
         
         pstmt4.execute();
         System.out.println("Record inserted......");*/
         
		/*PreparedStatement pstmt5 = ConnectionUtil.getConnection().prepareStatement("update bornbhukkad.bb_admin_panel_vendors_kirana set symbol=?, symbol_name=? where id=?");
		 
		 pstmt5.setString(1, base64String);
		 pstmt5.setString(2, "Sri_Laxmi_Kirana_General_Store.jpg");
         pstmt5.setString(3, "32");
         
         pstmt5.execute();
         System.out.println("Record inserted......");*/
         
    }
}
