package com.winesee.projectjong.config.security;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * DateFormat 커스텀
 */
@Component
public class DateFormatConfiguration {
    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder
                    .timeZone(TimeZone.getTimeZone("UTC"))
                    .simpleDateFormat(datetimeFormat)
                    .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(datetimeFormat)));
        };
    }
}
