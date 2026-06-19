package com.nmquan1503.backend_springboot.mappers.location;

import com.nmquan1503.backend_springboot.dtos.responses.location.ProvinceDetailResponse;
import com.nmquan1503.backend_springboot.entities.location.Province;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:09+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProvinceMapperImpl implements ProvinceMapper {

    @Override
    public ProvinceDetailResponse toProvinceDetailResponse(Province province) {
        if ( province == null ) {
            return null;
        }

        ProvinceDetailResponse.ProvinceDetailResponseBuilder provinceDetailResponse = ProvinceDetailResponse.builder();

        provinceDetailResponse.id( province.getId() );
        provinceDetailResponse.name( province.getName() );

        return provinceDetailResponse.build();
    }
}
