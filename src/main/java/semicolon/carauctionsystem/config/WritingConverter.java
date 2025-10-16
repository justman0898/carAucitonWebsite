package semicolon.carauctionsystem.config;

import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@org.springframework.data.convert.WritingConverter
public  class WritingConverter implements Converter<UUID, String> {


    @Override
    public String convert(UUID source) {
        return source.toString();
    }


}
