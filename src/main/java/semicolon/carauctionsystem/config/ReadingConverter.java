package semicolon.carauctionsystem.config;

import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@org.springframework.data.convert.ReadingConverter
public class ReadingConverter implements Converter <String, UUID>{
    @Override
    public UUID convert(String source) {
        return UUID.fromString(source);
    }
}
