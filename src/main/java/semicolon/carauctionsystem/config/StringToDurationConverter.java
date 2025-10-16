package semicolon.carauctionsystem.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Duration;

@ReadingConverter
public class StringToDurationConverter implements Converter<String, Duration> {
    @Override
    public Duration convert(String source) {
        if (source == null) return null;

        try {
            // Try normal ISO-8601 format first (e.g. PT1H)
            return Duration.parse(source);
        } catch (Exception ex) {
            // Fallback for "HH:mm:ss" format from DB
            String[] parts = source.split(":");
            if (parts.length == 3) {
                long hours = Long.parseLong(parts[0]);
                long minutes = Long.parseLong(parts[1]);
                long seconds = Long.parseLong(parts[2]);
                return Duration.ofHours(hours)
                        .plusMinutes(minutes)
                        .plusSeconds(seconds);
            }
            throw new IllegalArgumentException("Invalid duration format: " + source);
        }
    }

}
