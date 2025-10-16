package semicolon.carauctionsystem.config;


import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.Car;
import semicolon.carauctionsystem.auctions.data.models.CarImage;
import semicolon.carauctionsystem.auctions.data.models.VehicleHistory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableR2dbcRepositories("semicolon.carauctionsystem.auctions.data.repositories")
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public ReactiveTransactionManager connectionFactoryTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }


    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }


    @Override
    protected List<Object> getCustomConverters() {
        return Arrays.asList(
                new WritingConverter(),
                new ReadingConverter(),
                new DurationToLongConverter(),
                new StringToDurationConverter()
//                new LongToDurationConverter()
        );
    }

    @Bean
    public BeforeConvertCallback<Car> carIdGenerator() {
        return (car, table) -> {
            if (car.getId() == null) {
                car.setId(UUID.randomUUID());
            }
            return Mono.just(car);
        };
    }

    @Bean
    public BeforeConvertCallback<CarImage> carImageIdGenerator() {
        return (carImage, table) -> {
            if (carImage.getId() == null) {
                carImage.setId(UUID.randomUUID());
            }
            return Mono.just(carImage);
        };
    }

    @Bean
    public BeforeConvertCallback<VehicleHistory> vehicleHistoryIdGenerator() {
        return (vehicleHistory, table) -> {
            if (vehicleHistory.getId() == null) {
                vehicleHistory.setId(UUID.randomUUID());
            }
            return Mono.just(vehicleHistory);
        };
    }

    @Bean
    public BeforeConvertCallback<Auction> auctionIdGenerator() {
        return (auction, table) -> {
            if (auction.getId() == null) {
                auction.setId(UUID.randomUUID());
            }
            return Mono.just(auction);
        };
    }


}
