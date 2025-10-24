package semicolon.carauctionsystem.auctions.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.data.models.Car;
import semicolon.carauctionsystem.auctions.data.models.CarImage;
import semicolon.carauctionsystem.auctions.data.repositories.CarImages;
import semicolon.carauctionsystem.auctions.data.repositories.CarRepository;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;



@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @MockitoBean
    private CarRepository carRepository;

    @Autowired
    private CarService carService;
    @MockitoBean
    private CarImages carImages;

    @Test
    void testThatCanViewCarListing(){
        Car response = new Car();
        response.setId(UUID.randomUUID());
        response.setMake("Mercedes");

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());
        carImage.setFileName("img.jpg");

        CarImage carImage2 = new CarImage();
        carImage2.setId(carImage.getId());
        carImage2.setFileName("img2.jpg");

        when(carRepository.findById(any(UUID.class))).thenReturn(Mono.just(response));
        when(carImages.findByCarId(any())).thenReturn(Flux.just(carImage,  carImage2));

        CarResponseDto responseDto = carService.viewCar(UUID.randomUUID()).block();

        assertNotNull(responseDto);
        assertEquals(response.getMake(), responseDto.getMake());
        assertEquals(response.getId(), responseDto.getId());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img.jpg",  responseDto.getImageUrls().get(0));
    }

    @Test
    void testThatCanGetAllCars(){
        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setMake("Mercedes");
        car.setStatus(AuctionStatus.ENDED);


        Car car2 = new Car();
        car2.setId(UUID.randomUUID());
        car2.setMake("Lexus");
        car2.setStatus(AuctionStatus.NOT_STARTED);

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());
        carImage.setCarId(car2.getId());
        carImage.setFileName("img.jpg");

        CarImage carImage2 = new CarImage();
        carImage2.setId(carImage.getId());
        carImage2.setCarId(car2.getId());
        carImage2.setFileName("img2.jpg");

        when(carRepository.findAll()).thenReturn(Flux.just(car, car2));
        when(carImages.findByCarId(any())).thenReturn(Flux.just(carImage,   carImage2));


        List<CarResponseDto> cars = carService.getCars().collectList().block();
        assertEquals(1, cars.size());
        assertEquals(AuctionStatus.NOT_STARTED, cars.get(0).getStatus());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img.jpg",  cars.get(0).getImageUrls().get(0));


    }

}