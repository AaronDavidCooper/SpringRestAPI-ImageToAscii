package ascii.rest.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ascii.rest.entity.ImageAscii;

@RestController
public class asciiRestController {

	@PostMapping("/upload")
	public ImageAscii uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		
    	// Load image
    	BufferedImage originalImage = null;
    	originalImage = ImageIO.read(multipartToFile(file)); // using helper method to convert file
    	
    	// Downscale resolution by a quarter
    	int w = originalImage.getWidth() / 4;
        int h = originalImage.getHeight() / 4;
    	BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    	Graphics g = image.createGraphics();
    	g.drawImage(originalImage, 0, 0, w, h, null);
    	g.dispose();
		
    	// Create array of pixel data
    	int w2 = image.getWidth();
        int h2 = image.getHeight();        
        int[] imageData = image.getRGB(0, 0, w2, h2, null, 0, w2);
        
        // Populates 2 new arraylists, one for image brightness (ascii chars) & the other their hex color
        List<Integer> pixelBrightness = new ArrayList<Integer>();        
        List<String> hexColor = new ArrayList<String>(); 
        for(int i = 0; i < imageData.length; i++) {
        	Color pixel = new Color(imageData[i]);
        	String hex = String.format("#%02X%02X%02X", pixel.getRed(), pixel.getGreen(), pixel.getBlue());  
        	int brightness = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;  
        	hexColor.add(hex);
        	pixelBrightness.add(brightness);
        }
        
        // Character values used to replace brightness value
        String asciiSwap = "`^\\\",:;Il!i~+_-?][}{1)(|\\\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
        float convertValue = (float)255 / (asciiSwap.length()-1); // 255/66 = 3.8636363636 brightness per character        
        
        // Creates new Arraylist, populating with above char strings based on brightness value
        List<Character> asciiPixels = new ArrayList<Character>();
        for(int j = 0; j < pixelBrightness.size(); j++) {
        	int ascii = (int)(pixelBrightness.get(j) / convertValue);  
        	asciiPixels.add(asciiSwap.charAt(ascii));
        }    		
		
        // Creates pojo and loads it with the data
        ImageAscii output = new ImageAscii();        
        output.setHexColor(hexColor);
        output.setPixelAscii(asciiPixels);
        output.setWidth(w2);
		
        return output;
	}
	
	
	public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException 	{
	    File convFile = new File( multipart.getOriginalFilename());
	    multipart.transferTo(convFile);
	    return convFile;
	}
	
}
