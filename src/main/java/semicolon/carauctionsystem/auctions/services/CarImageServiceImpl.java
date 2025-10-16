package semicolon.carauctionsystem.auctions.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semicolon.carauctionsystem.auctions.exceptions.ImageNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarImageServiceImpl implements CarImageService {
    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + extension;

            Path url = Paths.get("upload/cars/");
            if (!Files.exists(url))  Files.createDirectories(url);
            Files.copy(file.getInputStream(), url.resolve(fileName));

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource getCarImages(String fileName) throws MalformedURLException {
            Path path = Paths.get("upload/cars/" + fileName);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new ImageNotFoundException();
            return resource;
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        return files.stream().map(this::uploadImage).collect(Collectors.toList());
    }
}
