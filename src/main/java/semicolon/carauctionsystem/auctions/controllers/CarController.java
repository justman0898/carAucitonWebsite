package semicolon.carauctionsystem.auctions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.auctions.services.CarService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@CrossOrigin("*")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCar(@PathVariable UUID id) {
        CarResponseDto foundCar = carService.viewCar(id).block();
        return ResponseEntity.ok(foundCar);
    }

    @GetMapping("/all-cars")
    public ResponseEntity<List<CarResponseDto>> getCars() {
        List<CarResponseDto> foundCars = carService.getCars().collectList().block();
        return ResponseEntity.ok(foundCars);
    }
}
