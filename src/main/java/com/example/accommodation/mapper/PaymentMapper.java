package com.example.accommodation.mapper;

import com.example.accommodation.config.MapperConfig;
import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.model.Payment;
import java.net.URL;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "sessionUrl", source = "sessionUrl", qualifiedByName = "urlToString")
    PaymentDto toDto(Payment payment);

    @Named("urlToString")
    default String urlToString(URL url) {
        return url == null ? null : url.toString();
    }
}
