package semicolon.carauctionsystem.auctions.dtos.request;

import lombok.Data;
import semicolon.carauctionsystem.auctions.data.models.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CarRequestDto {
    private UUID sellerId;
    private String make;
    private String trim;
    private Date year;
    private String vin;
    private BigInteger mileage;
    private ExteriorColor exteriorColor;
    private String engine;
    private Transmission transmission;
    private DriveTrain driveTrain;
    private BigDecimal price;
    private boolean hasKeys;
    private ConditionReport conditionReport;
    private List<String> imageFileNames;
    private List<String> vehicleHistoryFileNames;
    private DamageType damage;
}
