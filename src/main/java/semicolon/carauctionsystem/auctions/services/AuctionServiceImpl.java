package semicolon.carauctionsystem.auctions.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.*;
import semicolon.carauctionsystem.auctions.data.repositories.*;
import semicolon.carauctionsystem.auctions.dtos.request.ApprovalRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.AuctionRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.BidResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotFoundException;
import semicolon.carauctionsystem.auctions.exceptions.CarNotFoundException;
import semicolon.carauctionsystem.users.utils.Mapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {


    @Autowired
    private CarRepository carRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private CarImages carImages;
    @Autowired
    private VehicleHistories vehicleHistories;
    @Autowired
    private Mapper mapper;
    @Autowired
    private BidRepository bidRepository;


    @Override
    public Mono<AuctionResponseDto> createAuction(AuctionRequestDto requestDto) {
        return carRepository.existsById(requestDto.getCarId())
                .flatMap(exist ->{
                    if (!exist) return Mono.error(new CarNotFoundException());

                    Auction auction = new Auction();
                    auction.setSellerId(requestDto.getSellerId());
                    auction.setStatus(AuctionStatus.PENDING);
                    auction.setCurrentHighestBidAmount(requestDto.getStartBidAmount());
                    auction.setCarId(requestDto.getCarId());
                    auction.setCarLocation(requestDto.getCarLocation());
                    auction.setBuyNowPrice(requestDto.getBuyNowPrice());

                    return auctionRepository.save(auction)
                            .map(mapper::toDto)
                            .flatMap(response ->
                                carImages.findByCarId(response.getCarId()).collectList()
                                        .switchIfEmpty(Mono.error(new CarNotFoundException()))
                                        .flatMap(carImage -> {
                                            List<String> imageUrls =  carImage.stream().map(image-> "http://localhost:1000/api/v1/car-image/download/" + image.getFileName())
                                                    .toList();
                                            response.setImageUrls(imageUrls);
                                            return Mono.just(response);
                                        })
                            );
                });
    }

    @Override
    public Mono<Void> approveAuction(ApprovalRequestDto requestDto) {
        return auctionRepository.findById(requestDto.getAuctionId())
                .switchIfEmpty(Mono.error(new AuctionNotFoundException(requestDto.getAuctionId().toString())))
                .flatMap(found -> {
                    found.setStatus(requestDto.getAuctionStatus());
                    found.setStartDate(requestDto.getAuctionStartDate());
                    found.setEndDate(requestDto.getAuctionEndDate());
                    found.setBidDuration(requestDto.getAuctionDuration());
                    return auctionRepository.save(found);
                }).flatMap(auction ->
                    carRepository.findById(auction.getCarId())
                            .switchIfEmpty(Mono.error(new CarNotFoundException()))
                            .flatMap(car -> {
                                car.setStatus(AuctionStatus.NOT_STARTED);
                                        return carRepository.save(car);
                            })
                )
                .then();
    }

    @Override
    public Flux<AuctionResponseDto> getLiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.LIVE)
                .flatMapSequential(auction-> viewAuction(auction.getId()));
    }


    @Override
    public Mono<AuctionResponseDto> viewAuction(UUID auctionId) {
        return auctionRepository.findById(auctionId)
                .flatMap(auction ->
                        carRepository.findById(auction.getCarId())
                                .flatMap(car ->
                                        carImages.findByCarId(car.getId())
                                                .map(img -> "http://localhost:1000/api/v1/car-image/download/" + img.getFileName())
                                                .collectList()
                                                .map(urls -> {
                                                    AuctionResponseDto dto = mapper.toDto(auction);
                                                    dto.setCarMake(car.getMake());
                                                    dto.setYear(car.getYear());
                                                    dto.setCarTrim(car.getTrim());
                                                    dto.setConditionReport(car.getConditionReport());
                                                    dto.setImageUrls(urls);
                                                    return dto;
                                                })
                                )
                );
    }




    public Mono<CarResponseDto> listVehicle(CarRequestDto requestDto) {
        Car car = mapper.toEntity(requestDto);
        car.setStatus(AuctionStatus.PENDING);
        Mono<Car> savedCarMono = carRepository.save(car);
        String imageEndpoint = "http://localhost:1000/api/v1/car-image/download/";
        String vehicleHistoryEndpoint = "";

        return savedCarMono.flatMap(savedCar ->
                        Flux.fromIterable(Optional.ofNullable(requestDto.getImageFileNames())
                                        .orElse(Collections.emptyList()))
                                .flatMap(fileName -> {
                                    CarImage newImage = new CarImage();
                                    newImage.setCarId(savedCar.getId());
                                    newImage.setFileName(fileName);
                                    return carImages.save(newImage);
                                })
                                .then()
                                .then(
                                        Flux.fromIterable(Optional.ofNullable(requestDto.getVehicleHistoryFileNames())
                                                        .orElse(Collections.emptyList()))
                                                .flatMap(fileName -> {
                                                    VehicleHistory history = new VehicleHistory();
                                                    history.setCarId(savedCar.getId());
                                                    history.setFileName(fileName);
                                                    return vehicleHistories.save(history);
                                                })
                                                .then()
                                )
                                .then(Mono.just(savedCar))
                )
                .map(mapper::toDto)
                .map(savedResponse->{
                    List<String> imageUrls = Optional.ofNullable(requestDto.getImageFileNames())
                            .orElse(Collections.emptyList())
                            .stream().map(fileName ->  imageEndpoint + fileName).toList();

                    List<String> vehicleHistoryUrls = Optional.ofNullable(requestDto.getVehicleHistoryFileNames())
                            .orElse(Collections.emptyList())
                            .stream().map(fileName -> vehicleHistoryEndpoint + fileName).toList();

                    savedResponse.setImageUrls(imageUrls);
                    savedResponse.setVehicleHistoryUrl(vehicleHistoryUrls);
                    return savedResponse;
                });
    }

    @Override
    public Flux<AuctionResponseDto> viewAuctionListing(UUID sellerId) {
        return carRepository.findBySellerId(sellerId)
                .flatMap(car->
                    carImages.findByCarId(car.getId()).collectList()
                            .flatMap(images->
                                auctionRepository.findByCarId(car.getId())
                                        .flatMap(auction->
                                                bidRepository.findBidsByAuctionId(auction.getId()).collectList()
                                                        .map(bids -> {
                                                            AuctionResponseDto dto = mapper.toDto(auction);
                                                            List<BidResponseDto> bidResponseDto = bids.stream().map(bid->mapper.toDto(bid)).toList();
                                                            dto.setBids(bidResponseDto);
                                                            dto.setCarMake(car.getMake());
                                                            dto.setConditionReport(car.getConditionReport());
                                                            dto.setCarTrim(car.getTrim());
                                                            dto.setYear(car.getYear());
                                                            List<String> urlEndpoints = images.stream().map(image->image.getImageUrl() + image.getFileName()).toList();
                                                            dto.setImageUrls(urlEndpoints);

                                                            return dto;
                                                        })
                                        )
                            )
                );
    }

    @Override
    public Flux<AuctionResponseDto> getAllApprovedAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.NOT_STARTED)
                .flatMapSequential(auction-> viewAuction(auction.getId()));
    }


}
