package semicolon.carauctionsystem.auctions.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import semicolon.carauctionsystem.auctions.data.models.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CarRequestDto {
    @NotNull
    private UUID sellerId;
    @NotBlank
    private String make;
    @NotBlank
    private String trim;
    @NotNull(message = "Year is compulsory")
    private Date year;
    @NotBlank
    private String vin;
    @NotNull
    private BigInteger mileage;
    @NotNull
    private ExteriorColor exteriorColor;
    @NotBlank
    private String engine;
    @NotNull
    private Transmission transmission;
    @NotNull
    private DriveTrain driveTrain;
    @NotNull
    private BigDecimal price;
    @NotNull
    private boolean hasKeys;
    @NotNull
    private ConditionReport conditionReport;
    @NotNull
    private List<String> imageFileNames;
    @NotNull
    private List<String> vehicleHistoryFileNames;
    @NotNull
    private DamageType damage;
}
