package semicolon.carauctionsystem.auctions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.data.models.Car;
import semicolon.carauctionsystem.auctions.data.models.CarImage;
import semicolon.carauctionsystem.auctions.data.repositories.CarImages;
import semicolon.carauctionsystem.auctions.data.repositories.CarRepository;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.users.utils.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class CarServiceImpl implements CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private CarImages carImages;

    private static CarResponseDto loadImage(Tuple2<CarResponseDto, List<CarImage>> tuple) {
        List<CarImage> images = tuple.getT2();
        List<String> url = images.stream()
                .map(img -> "http://localhost:1000/api/v1/car-image/download/" + img.getFileName())
                .toList();

        CarResponseDto foundCar = tuple.getT1();
        foundCar.setImageUrls(url);
        return foundCar;
    }


    @Override
    public Mono<CarResponseDto> viewCar(UUID id) {
        Mono<Car> responseDto = carRepository.findById(id);
        return responseDto.map(mapper::toDto)
                .flatMap(response ->
                    carImages.findByCarId(response.getId())
                            .collectList()
                .map(images ->
                    Tuples.of(response, images))
                )
                .map(CarServiceImpl::loadImage);




    }

    @Override
    public Flux<CarResponseDto> getCars() {
        Flux<Car> cars = carRepository.findAll();
        return cars.filter(car -> car.getStatus().equals(AuctionStatus.LIVE) || car.getStatus().equals(AuctionStatus.NOT_STARTED))
                .map(mapper::toDto)
                .flatMap(response ->
                        carImages.findByCarId(response.getId())
                                .collectList()
                                .map(images ->
                                        Tuples.of(response, images))
                ).map(CarServiceImpl::loadImage);

    }

    @Override
    public Flux<CarResponseDto> getCarsByMake(String make) {
        return null;
    }

    @Override
    public Flux<CarResponseDto> getCarsByVin(String model) {
        return null;
    }

    @Override
    public Flux<CarResponseDto> getCarsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return null;
    }
}
