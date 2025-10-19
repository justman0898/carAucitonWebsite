package semicolon.carauctionsystem.auctions.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface CarService {
    Mono<CarResponseDto> viewCar(UUID id);
    Flux<CarResponseDto> getCars();
    Flux<CarResponseDto> getCarsByMake(String make);
    Flux<CarResponseDto> getCarsByVin(String model);
    Flux<CarResponseDto> getCarsByPrice(BigDecimal minPrice, BigDecimal maxPrice);
}
