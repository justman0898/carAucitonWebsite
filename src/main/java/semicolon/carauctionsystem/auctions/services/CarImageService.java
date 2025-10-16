package semicolon.carauctionsystem.auctions.services;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

public interface CarImageService {
    String uploadImage(MultipartFile file);
    Resource getCarImages(String fileName)throws MalformedURLException;
    List<String> uploadImages(List<MultipartFile> files) ;

}
