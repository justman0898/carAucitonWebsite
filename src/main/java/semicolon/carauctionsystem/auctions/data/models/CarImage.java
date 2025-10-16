package semicolon.carauctionsystem.auctions.data.models;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;
@Data
@Table("car_image")
public class CarImage {

    private UUID id;

    private UUID carId;

    private String fileName;


    private String imageUrl = "http://localhost:1000/api/v1/car-image/download/";
}
