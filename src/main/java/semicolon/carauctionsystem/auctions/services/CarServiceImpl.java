package semicolon.carauctionsystem.auctions.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class CarServiceImpl implements CarService {
    @Override
    public Mono<CarResponseDto> viewCar(UUID id) {
        return null;
    }

    @Override
    public Flux<CarResponseDto> getCars() {
        return null;
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
