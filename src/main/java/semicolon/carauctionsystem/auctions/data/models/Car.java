package semicolon.carauctionsystem.auctions.data.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Table("car")
@Data
public class Car {

    @Id
    private UUID id;

    private String make;

    private String trim;

    private LocalDate year;

    private String vin;

    private BigInteger mileage;

    private UUID sellerId;

    private ExteriorColor exteriorColor;


    private String engine;


    private Transmission transmission;


    private DriveTrain driveTrain;

    private BigDecimal price;

    private boolean hasKeys;

    private DamageType damage;

    private ConditionReport conditionReport;

    private AuctionStatus status;

    @Transient
    private List<VehicleHistory> vehicleHistory;

    @Transient
    private List<CarImage> imageUrls;




}
