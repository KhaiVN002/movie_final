package com.nmquan1503.backend_springboot.services.theater;

import com.nmquan1503.backend_springboot.repositories.theater.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomDetailResponse;
import com.nmquan1503.backend_springboot.mappers.theater.RoomMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    RoomMapper roomMapper;

    public boolean existsByRoomId(Integer roomId) {
        return roomRepository.existsById(roomId);
    }

    public List<RoomDetailResponse> getRoomsByBranchId(Short branchId) {
        return roomRepository.findByBranchId(branchId).stream()
                .map(roomMapper::toRoomDetailResponse)
                .toList();
    }

}
