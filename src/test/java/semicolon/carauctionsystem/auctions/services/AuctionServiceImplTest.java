package semicolon.carauctionsystem.auctions.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import semicolon.carauctionsystem.auctions.data.models.*;
import semicolon.carauctionsystem.auctions.data.repositories.*;
import semicolon.carauctionsystem.auctions.dtos.request.ApprovalRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.AuctionRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotFoundException;
import semicolon.carauctionsystem.auctions.exceptions.CarNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @MockitoBean
    private AuctionRepository auctionRepository;

    @MockitoBean
    private CarRepository carRepository;

    @MockitoBean
    private BidRepository bidRepository;

    @Autowired
    private AuctionService auctionService;

    @Captor
    private ArgumentCaptor<Auction> auctionArgumentCaptor;

    @Captor
    private ArgumentCaptor<Car> carArgumentCaptor;

    @Captor
    private ArgumentCaptor<CarImage> carImagesArgumentCaptor;

    private final UUID carId = UUID.randomUUID();
    private final UUID sellerId = UUID.randomUUID();
    @MockitoBean
    private VehicleHistories vehicleHistories;
    @MockitoBean
    private CarImages carImages;

    @Test
    void testThatCanCreateAuctionListing(){
        AuctionRequestDto requestDto = new AuctionRequestDto();
        requestDto.setCarId(carId);
        requestDto.setSellerId(sellerId);

        Car car = new Car();
        car.setId(carId);

        CarImage carImage = new CarImage();
        carImage.setCarId(UUID.randomUUID());
        carImage.setFileName("img1");

        CarImage carImage2 = new CarImage();
        carImage2.setCarId(UUID.randomUUID());
        carImage2.setFileName("img2");

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setStatus(AuctionStatus.PENDING);
        auction.setCarId(carId);
        auction.setSellerId(sellerId);

        when(carRepository.existsById(carId)).thenReturn(Mono.just(true));
        when(auctionRepository.save(any(Auction.class))).thenReturn(Mono.just(auction));
        when(carImages.findByCarId(any())).thenReturn(Flux.just(carImage, carImage2));

        Mono<AuctionResponseDto> saved =  auctionService.createAuction(requestDto);

        StepVerifier.create(saved)
                .expectNextMatches(result->
                    result.getId().equals(auction.getId()) &&
                    result.getStatus().equals(AuctionStatus.PENDING)&&
                    result.getImageUrls().get(0).equals("http://localhost:1000/api/v1/car-image/download/img1")
                ).verifyComplete();

        verify(auctionRepository).save(any(Auction.class));

    }

    @Test
    void testThatThrowsExceptionWhenCarHasNotYetBeenUploaded(){
        when(carRepository.existsById(any(UUID.class))).thenReturn(Mono.just(false));
        when(auctionRepository.save(any(Auction.class))).thenReturn(Mono.just(new Auction()));


        AuctionRequestDto requestDto = new AuctionRequestDto();
        requestDto.setCarId(carId);
        requestDto.setSellerId(sellerId);

        Mono<AuctionResponseDto> saved =  auctionService.createAuction(requestDto);

        StepVerifier.create(saved)
                .expectErrorMatches(ex-> ex instanceof CarNotFoundException
                        && ex.getMessage().equals("Car Not Found"))
                .verify();
    }

    @Test
    void testThatAdminCanApprovePendingAuction(){
        Car car = new Car();
        car.setId(carId);

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setStatus(AuctionStatus.PENDING);
        auction.setCarId(carId);
        auction.setSellerId(sellerId);

        when(auctionRepository.findById(any(UUID.class))).thenReturn(Mono.just(auction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(Mono.just(auction));
        when(carRepository.findById(any(UUID.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any())).thenReturn(Mono.just(car));

        ApprovalRequestDto approvalRequestDto = new ApprovalRequestDto();
        approvalRequestDto.setAuctionId(auction.getId());
        approvalRequestDto.setAuctionStatus(AuctionStatus.NOT_STARTED);

        auctionService.approveAuction(approvalRequestDto).block();

        verify(auctionRepository).save(auctionArgumentCaptor.capture());
        Auction capturedAuction = auctionArgumentCaptor.getValue();
        assertEquals(AuctionStatus.NOT_STARTED, capturedAuction.getStatus());
    }

    @Test
    void testThatThrowsExceptionWhenAuctionHasNotYetBeenUploaded(){
        when(auctionRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        ApprovalRequestDto approvalRequestDto = new ApprovalRequestDto();
        approvalRequestDto.setAuctionId(UUID.randomUUID());
        approvalRequestDto.setAuctionStatus(AuctionStatus.NOT_STARTED);

        Mono<Void> approved = auctionService.approveAuction(approvalRequestDto);

        StepVerifier.create(approved)
                .expectErrorMatches(ex-> ex instanceof AuctionNotFoundException)
                .verify();
    }

    @Test
    void testThatCanViewAuction(){
        Car car = new Car();
        car.setId(carId);

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setStatus(AuctionStatus.PENDING);
        auction.setCarId(carId);
        auction.setSellerId(sellerId);

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());

        when(auctionRepository.findById(any(UUID.class))).thenReturn(Mono.just(auction));
        when(carRepository.findById(any(UUID.class))).thenReturn(Mono.just(car));
        when(carImages.findByCarId(any(UUID.class))).thenReturn(Flux.just(carImage));

        AuctionResponseDto responseDto = auctionService.viewAuction(UUID.randomUUID()).block();

        assertNotNull(responseDto);
        assertEquals(carId, responseDto.getCarId());
    }

    @Test
    void testThatCanListVehiclesForAuction(){
        Car car = new Car();
        car.setId(carId);

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(vehicleHistories.save(any(VehicleHistory.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(carImages.save(any(CarImage.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        CarRequestDto  requestDto = new CarRequestDto();
        requestDto.setImageFileNames(List.of("img1.jpg", "img2.jpg", "img3.jpg"));
        requestDto.setMake("test");

        CarResponseDto pendingApproval = auctionService.listVehicle(requestDto).block();

        verify(carRepository).save(carArgumentCaptor.capture());
        Car capturedAuction = carArgumentCaptor.getValue();
        assertEquals(AuctionStatus.PENDING, capturedAuction.getStatus());

        verify(carImages, times(3)).save(any(CarImage.class));
        assertNotNull(pendingApproval);
        assertEquals(AuctionStatus.PENDING, pendingApproval.getStatus());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img1.jpg", pendingApproval.getImageUrls().get(0));

    }

    @Test
    void testThatCanViewListing(){
        Car car = new Car();
        car.setId(carId);

        Car car2 = new Car();
        car2.setId(UUID.randomUUID());

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setCarId(carId);

        Auction auction2 = new Auction();
        auction2.setId(UUID.randomUUID());
        auction2.setCarId(car2.getId());

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());
        carImage.setCarId(carId);
        carImage.setFileName("img1.jpg");

        CarImage carImage2 = new CarImage();
        carImage2.setId(UUID.randomUUID());
        carImage2.setCarId(car2.getId());
        carImage2.setFileName("img2.jpg");


        when(carRepository.findBySellerId(any(UUID.class))).thenReturn(Flux.just(car, car2));
        when(auctionRepository.findByCarId(any(UUID.class))).thenReturn(Mono.just(auction))
                        .thenReturn(Mono.just(auction2));
        when(bidRepository.findBidsByAuctionId(any(UUID.class))).thenReturn(Flux.just(new Bid()));
        when(carImages.findByCarId(any(UUID.class))).thenReturn(Flux.just(carImage, carImage2));

        Flux<AuctionResponseDto> listings = auctionService.viewAuctionListing(UUID.randomUUID());
        List<AuctionResponseDto> viewListing = listings.collectList().block();

        assertNotNull(viewListing);
        assertEquals(2, viewListing.size());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img1.jpg",  viewListing.get(0).getImageUrls().get(0));
        assertEquals(car.getId(), viewListing.get(0).getCarId());
        assertEquals(car2.getId(), viewListing.get(1).getCarId());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img2.jpg", viewListing.get(1).getImageUrls().get(1));

    }

    @Test
    void testThatCanViewAllLiveAuctions(){
        Car car = new Car();
        car.setId(carId);

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setCarId(carId);
        auction.setStatus(AuctionStatus.LIVE);

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());
        carImage.setCarId(carId);
        carImage.setFileName("img1.jpg");

        when(auctionRepository.findByStatus(any(AuctionStatus.class))).thenReturn(Flux.just(auction));
        when(auctionRepository.findById(any(UUID.class))).thenReturn(Mono.just(auction));
        when(carRepository.findById(any(UUID.class))).thenReturn(Mono.just(car));
        when(carImages.findByCarId(any(UUID.class))).thenReturn(Flux.just(carImage));

        List<AuctionResponseDto> auctionResponseDto = auctionService.getLiveAuctions().collectList().block();
        assertNotNull(auctionResponseDto);
        assertEquals("http://localhost:1000/api/v1/car-image/download/img1.jpg", auctionResponseDto.get(0).getImageUrls().get(0));

    }

    @Test
    void testThatCanViewAllApprovedAuctions(){
        Car car = new Car();
        car.setId(carId);

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        auction.setCarId(carId);
        auction.setStatus(AuctionStatus.NOT_STARTED);

        CarImage carImage = new CarImage();
        carImage.setId(UUID.randomUUID());
        carImage.setCarId(carId);
        carImage.setFileName("img1.jpg");

        when(auctionRepository.findByStatus(AuctionStatus.NOT_STARTED)).thenReturn(Flux.just(auction));
        when(auctionRepository.findById(any(UUID.class))).thenReturn(Mono.just(auction));
        when(carRepository.findById(any(UUID.class))).thenReturn(Mono.just(car));
        when(carImages.findByCarId(any(UUID.class))).thenReturn(Flux.just(carImage));

        List<AuctionResponseDto> auctionResponseDto = auctionService.getAllApprovedAuctions().collectList().block();
        assertNotNull(auctionResponseDto);
        verify(auctionRepository, times(1)).findByStatus(AuctionStatus.NOT_STARTED);
        verify(auctionRepository, times(1)).findById(any(UUID.class));
        verify(carRepository, times(1)).findById(any(UUID.class));
        verify(carImages, times(1)).findByCarId(any(UUID.class));
        assertEquals(1,  auctionResponseDto.size());
        assertEquals("http://localhost:1000/api/v1/car-image/download/img1.jpg", auctionResponseDto.get(0).getImageUrls().get(0));

    }




}