package semicolon.carauctionsystem.auctions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import semicolon.carauctionsystem.auctions.services.CarImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/car-image")
public class UserImageRouter {

    @Autowired
    private CarImageService carImageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file){
        String fileName = carImageService.uploadImage(file);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/upload-images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files){
        List<String> imageFilenames = carImageService.uploadImages(files);
        return ResponseEntity.ok(imageFilenames);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadImage(@PathVariable String filename){
        try {
            Resource resource = carImageService.getCarImages(filename);
            Path path = resource.getFile().toPath();
//            Path path = Paths.get("upload/cars/").resolve(filename).normalize();

//            String contentType = Files.probeContentType(Paths.get(path.toUri()));
            String contentType = Files.probeContentType(path);

            if(contentType == null || contentType.isBlank()) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException  e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }







}
