package semicolon.carauctionsystem.auctions.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import semicolon.carauctionsystem.auctions.services.CarImageService;
import semicolon.carauctionsystem.users.services.JwtService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserImageRouter.class, excludeAutoConfiguration =  { SecurityAutoConfiguration.class })
class UserImageRouterTest {

    @MockitoBean
    private CarImageService carImageService;

    @MockitoBean
    private JwtService  jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testThatCanUpLoadImage() throws Exception {
        String filename = UUID.randomUUID().toString();
        when(carImageService.uploadImage(any(MultipartFile.class))).thenReturn(filename);

        MultipartFile image = new MockMultipartFile("file", "car.img",
                MediaType.IMAGE_JPEG_VALUE, "fake-car-image".getBytes());

        mockMvc.perform(multipart("/api/v1/car-image/upload")
                    .file((MockMultipartFile) image))
                .andExpect(status().isOk())
                    .andExpect(content().string(filename));

        verify(carImageService).uploadImage(any());
    }

    @Test
    void testThatCanUploadSeveralImages() throws Exception {
        String image1 = UUID.randomUUID().toString();
        String image2 = UUID.randomUUID().toString();
        List<String> imageFilenames = List.of(image1, image2);

        when(carImageService.uploadImages(anyList())).thenReturn(imageFilenames);

        MockMultipartFile image = new MockMultipartFile("files", "car.img",
                MediaType.IMAGE_JPEG_VALUE, "fake-car-image".getBytes());

        MockMultipartFile secondImage = new MockMultipartFile("files", "car.img",
                MediaType.IMAGE_JPEG_VALUE, "fake-car-image".getBytes());

        mockMvc.perform(multipart("/api/v1/car-image/upload-images")
                .file(image)
                .file(secondImage))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(imageFilenames)));
        verify(carImageService).uploadImages(anyList());
    }

    @Test
    void testCanDownloadImage() throws Exception {
        String filename = "676f42c7-2563-4a93-84e1-ddec81d368b7.jpg";
        Path path = Paths.get("upload/cars/" + filename);

        Resource resource = new UrlResource(path.toUri());
        when(carImageService.getCarImages(any())).thenReturn(resource);

        mockMvc.perform(get("/api/v1/car-image/download/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andReturn();

        verify(carImageService).getCarImages(any());

    }




}