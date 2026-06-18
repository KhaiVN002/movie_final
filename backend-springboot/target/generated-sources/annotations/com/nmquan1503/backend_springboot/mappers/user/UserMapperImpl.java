package com.nmquan1503.backend_springboot.mappers.user;

import com.nmquan1503.backend_springboot.dtos.requests.user.UserCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.user.UserUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.location.DistrictODetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.location.ProvinceDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.location.WardDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.GenderOptionResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserPreviewResponse;
import com.nmquan1503.backend_springboot.entities.location.District;
import com.nmquan1503.backend_springboot.entities.location.Province;
import com.nmquan1503.backend_springboot.entities.location.Ward;
import com.nmquan1503.backend_springboot.entities.user.Gender;
import com.nmquan1503.backend_springboot.entities.user.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.fullName( request.getFullName() );
        user.phone( request.getPhone() );
        user.email( request.getEmail() );
        user.password( request.getPassword() );
        user.birthday( request.getBirthday() );
        user.specificAddress( request.getSpecificAddress() );

        return user.build();
    }

    @Override
    public void updateUser(User user, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getFullName() != null ) {
            user.setFullName( request.getFullName() );
        }
        if ( request.getAvatarURL() != null ) {
            user.setAvatarURL( request.getAvatarURL() );
        }
        if ( request.getPassword() != null ) {
            user.setPassword( request.getPassword() );
        }
        if ( request.getSpecificAddress() != null ) {
            user.setSpecificAddress( request.getSpecificAddress() );
        }
    }

    @Override
    public UserDetailResponse toUserDetailResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserDetailResponse.UserDetailResponseBuilder userDetailResponse = UserDetailResponse.builder();

        userDetailResponse.id( user.getId() );
        userDetailResponse.fullName( user.getFullName() );
        userDetailResponse.avatarURL( user.getAvatarURL() );
        userDetailResponse.phone( user.getPhone() );
        userDetailResponse.email( user.getEmail() );
        if ( user.getBirthday() != null ) {
            userDetailResponse.birthday( user.getBirthday().atStartOfDay() );
        }
        userDetailResponse.gender( genderToGenderOptionResponse( user.getGender() ) );
        userDetailResponse.ward( wardToWardDetailResponse( user.getWard() ) );
        userDetailResponse.specificAddress( user.getSpecificAddress() );

        return userDetailResponse.build();
    }

    @Override
    public UserPreviewResponse toUserPreviewResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserPreviewResponse.UserPreviewResponseBuilder userPreviewResponse = UserPreviewResponse.builder();

        userPreviewResponse.id( user.getId() );
        userPreviewResponse.fullName( user.getFullName() );
        userPreviewResponse.avatarURL( user.getAvatarURL() );

        return userPreviewResponse.build();
    }

    protected GenderOptionResponse genderToGenderOptionResponse(Gender gender) {
        if ( gender == null ) {
            return null;
        }

        GenderOptionResponse.GenderOptionResponseBuilder genderOptionResponse = GenderOptionResponse.builder();

        genderOptionResponse.id( gender.getId() );
        genderOptionResponse.name( gender.getName() );

        return genderOptionResponse.build();
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

    protected WardDetailResponse wardToWardDetailResponse(Ward ward) {
        if ( ward == null ) {
            return null;
        }

        WardDetailResponse.WardDetailResponseBuilder wardDetailResponse = WardDetailResponse.builder();

        wardDetailResponse.id( ward.getId() );
        wardDetailResponse.name( ward.getName() );
        wardDetailResponse.district( districtToDistrictODetailResponse( ward.getDistrict() ) );

        return wardDetailResponse.build();
    }
}
