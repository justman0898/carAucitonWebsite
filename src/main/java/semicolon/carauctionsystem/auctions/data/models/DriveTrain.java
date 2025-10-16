package semicolon.carauctionsystem.auctions.data.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DriveTrain {
    TWO_WD("2WD"),
    FOUR_WD("4WD");

    public final String label;

    @JsonValue
    public String getLabel(){
        return label;
    }

    @JsonCreator
    public static DriveTrain fromLabel(String label) {
        for (DriveTrain dt : values()) {
            if (dt.label.equalsIgnoreCase(label)) {
                return dt;
            }
        }
        throw new IllegalArgumentException("Invalid DriveTrain label: " + label);
    }
}
