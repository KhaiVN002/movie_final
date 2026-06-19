package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.location.ProvinceDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.BranchOptionResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.BranchSimpleResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.BranchStatusResponse;
import com.nmquan1503.backend_springboot.entities.location.District;
import com.nmquan1503.backend_springboot.entities.location.Province;
import com.nmquan1503.backend_springboot.entities.location.Ward;
import com.nmquan1503.backend_springboot.entities.theater.Branch;
import com.nmquan1503.backend_springboot.entities.theater.BranchStatus;
import com.nmquan1503.backend_springboot.mappers.location.ProvinceMapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:08+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BranchMapperImpl implements BranchMapper {

    @Autowired
    private ProvinceMapper provinceMapper;

    @Override
    public BranchOptionResponse toBranchOptionResponse(Branch branch) {
        if ( branch == null ) {
            return null;
        }

        ProvinceDetailResponse province = null;
        Short id = null;
        String name = null;
        BranchStatusResponse status = null;

        province = provinceMapper.toProvinceDetailResponse( branchWardDistrictProvince( branch ) );
        id = branch.getId();
        name = branch.getName();
        status = branchStatusToBranchStatusResponse( branch.getStatus() );

        BranchOptionResponse branchOptionResponse = new BranchOptionResponse( id, name, province, status );

        return branchOptionResponse;
    }

    @Override
    public BranchSimpleResponse toBranchSimpleResponse(Branch branch) {
        if ( branch == null ) {
            return null;
        }

        BranchSimpleResponse.BranchSimpleResponseBuilder branchSimpleResponse = BranchSimpleResponse.builder();

        branchSimpleResponse.id( branch.getId() );
        branchSimpleResponse.name( branch.getName() );

        return branchSimpleResponse.build();
    }

    @Override
    public List<BranchOptionResponse> toListBranchOptionResponse(List<Branch> branches) {
        if ( branches == null ) {
            return null;
        }

        List<BranchOptionResponse> list = new ArrayList<BranchOptionResponse>( branches.size() );
        for ( Branch branch : branches ) {
            list.add( toBranchOptionResponse( branch ) );
        }

        return list;
    }

    @Override
    public List<BranchSimpleResponse> toListBranchSimpleResponse(List<Branch> branches) {
        if ( branches == null ) {
            return null;
        }

        List<BranchSimpleResponse> list = new ArrayList<BranchSimpleResponse>( branches.size() );
        for ( Branch branch : branches ) {
            list.add( toBranchSimpleResponse( branch ) );
        }

        return list;
    }

    private Province branchWardDistrictProvince(Branch branch) {
        Ward ward = branch.getWard();
        if ( ward == null ) {
            return null;
        }
        District district = ward.getDistrict();
        if ( district == null ) {
            return null;
        }
        return district.getProvince();
    }

    protected BranchStatusResponse branchStatusToBranchStatusResponse(BranchStatus branchStatus) {
        if ( branchStatus == null ) {
            return null;
        }

        BranchStatusResponse.BranchStatusResponseBuilder branchStatusResponse = BranchStatusResponse.builder();

        branchStatusResponse.id( branchStatus.getId() );
        branchStatusResponse.name( branchStatus.getName() );

        return branchStatusResponse.build();
    }
}
