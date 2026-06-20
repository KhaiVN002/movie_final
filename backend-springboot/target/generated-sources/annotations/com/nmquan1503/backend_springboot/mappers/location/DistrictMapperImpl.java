package com.nmquan1503.backend_springboot.mappers.location;

import com.nmquan1503.backend_springboot.dtos.responses.location.DistrictODetailResponse;
import com.nmquan1503.backend_springboot.entities.location.District;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DistrictMapperImpl implements DistrictMapper {

    @Autowired
    private ProvinceMapper provinceMapper;

    @Override
    public DistrictODetailResponse toDistrictDetailResponse(District district) {
        if ( district == null ) {
            return null;
        }

        DistrictODetailResponse.DistrictODetailResponseBuilder districtODetailResponse = DistrictODetailResponse.builder();

        districtODetailResponse.id( district.getId() );
        districtODetailResponse.name( district.getName() );
        districtODetailResponse.province( provinceMapper.toProvinceDetailResponse( district.getProvince() ) );

        return districtODetailResponse.build();
    }
}
