package semicolon.carauctionsystem.config;

import org.springframework.core.convert.converter.Converter;

import java.time.Duration;

public class DurationToLongConverter implements Converter<Duration, Long> {
    @Override
    public Long convert(Duration source) {
        return (source == null) ? null : source.getSeconds();
    }
}
