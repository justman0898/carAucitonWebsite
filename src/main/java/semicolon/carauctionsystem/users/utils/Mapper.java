package semicolon.carauctionsystem.users.utils;

import org.mapstruct.Mapping;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.data.models.Car;
import semicolon.carauctionsystem.auctions.data.models.Wallet;
import semicolon.carauctionsystem.auctions.dtos.WalletResponse;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.BidResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.users.data.models.Kyc;
import semicolon.carauctionsystem.users.data.models.User;
import semicolon.carauctionsystem.users.dtos.KycRequestDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {


    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles",  ignore = true)
    @Mapping(target = "kycStatus", ignore = true)
    User toEntity(RegisterRequestDto registerRequestDto);

    WalletResponse toDto(Wallet wallet);

    @Mapping(target = "carMake", ignore = true)
    @Mapping(target = "carTrim", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "conditionReport",  ignore = true)
    @Mapping(target = "bids", ignore = true)
    AuctionResponseDto toDto (Auction auction);

    @Mapping(target = "vehicleHistory", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    Car toEntity(CarRequestDto  carRequestDto);

    @Mapping(target = "vehicleHistoryUrl", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(source = "status", target = "status")
    CarResponseDto toDto(Car car);

    Kyc toEntity (KycRequestDto kycRequestDto);

    BidResponseDto toDto (Bid bid);

}
