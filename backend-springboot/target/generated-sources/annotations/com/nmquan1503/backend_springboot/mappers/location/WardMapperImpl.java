package com.nmquan1503.backend_springboot.mappers.location;

import com.nmquan1503.backend_springboot.dtos.responses.location.DistrictODetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.location.ProvinceDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.location.WardDetailResponse;
import com.nmquan1503.backend_springboot.entities.location.District;
import com.nmquan1503.backend_springboot.entities.location.Province;
import com.nmquan1503.backend_springboot.entities.location.Ward;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class WardMapperImpl implements WardMapper {

    @Override
    public WardDetailResponse toWardDetailResponse(Ward ward) {
        if ( ward == null ) {
            return null;
        }

        WardDetailResponse.WardDetailResponseBuilder wardDetailResponse = WardDetailResponse.builder();

        wardDetailResponse.id( ward.getId() );
        wardDetailResponse.name( ward.getName() );
        wardDetailResponse.district( districtToDistrictODetailResponse( ward.getDistrict() ) );

        return wardDetailResponse.build();
    }

    protected ProvinceDetailResponse provinceToProvinceDetailResponse(Province province) {
        if ( province == null ) {
            return null;
        }

        ProvinceDetailResponse.ProvinceDetailResponseBuilder provinceDetailResponse = ProvinceDetailResponse.builder();

        provinceDetailResponse.id( province.getId() );
        provinceDetailResponse.name( province.getName() );

        return provinceDetailResponse.build();
    }

    protected DistrictODetailResponse districtToDistrictODetailResponse(District district) {
        if ( district == null ) {
            return null;
        }

        DistrictODetailResponse.DistrictODetailResponseBuilder districtODetailResponse = DistrictODetailResponse.builder();

        districtODetailResponse.id( district.getId() );
        districtODetailResponse.name( district.getName() );
        districtODetailResponse.province( provinceToProvinceDetailResponse( district.getProvince() ) );

        return districtODetailResponse.build();
    }
}
