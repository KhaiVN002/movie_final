package com.nmquan1503.backend_springboot.services.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatDetailResponse;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.theater.SeatMapper;
import com.nmquan1503.backend_springboot.repositories.theater.SeatRepository;
import com.nmquan1503.backend_springboot.services.reservation.SeatHoldService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatService {

    SeatRepository seatRepository;

    SeatMapper seatMapper;

    StringRedisTemplate redisTemplate;
    SeatHoldService seatHoldService;

    public List<SeatDetailResponse> getSeatDetailsByShowtimeId(Long showtimeId) {
        List<Seat> seats = seatRepository.findByShowtimeId(showtimeId);
        List<Long> lockedSeatIds = seatHoldService.getActiveSeatIds(showtimeId);
        Set<Long> setLockedSeatIds = new HashSet<>(lockedSeatIds);
        List<SeatDetailResponse> responses = seatMapper.toListSeatDetailResponse(seats);
        for (SeatDetailResponse response : responses) {
            response.setLocked(setLockedSeatIds.contains(response.getId()));
        }
        return responses;
    }

    public List<Seat> fetchByIds(List<Long> ids) {
        List<Seat> seats = seatRepository.findAllById(ids);
        if (seats.size() < ids.size()) {
            throw new GeneralException(ResponseCode.SEAT_NOT_FOUND);
        }
        return seats;
    }

    public boolean tryLockSeats(Long showtimeId, List<Long> seatIds, String ownerToken, long ttlSeconds) {
        List<String> keys = generateListSeatLockedKeys(showtimeId, seatIds);
        String luaScript = """
                for i = 1, #KEYS do
                    if redis.call('exists', KEYS[i]) == 1 then
                        return 0
                    end
                end
                for i = 1, #KEYS do
                    redis.call('set', KEYS[i], ARGV[1], 'EX', ARGV[2])
                end
                return 1
                """;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(
                script,
                keys,
                ownerToken,
                String.valueOf(ttlSeconds)
        );
        return Long.valueOf(1L).equals(result);
    }

    public void unlockSeats(Long showtimeId, List<Long> seatIds, String ownerToken) {
        List<String> keys = generateListSeatLockedKeys(showtimeId, seatIds);
        String luaScript = """
                local deleted = 0
                for i = 1, #KEYS do
                    if redis.call('get', KEYS[i]) == ARGV[1] then
                        deleted = deleted + redis.call('del', KEYS[i])
                    end
                end
                return deleted
                """;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        redisTemplate.execute(
                script,
                keys,
                ownerToken
        );
    }

    private String generateSeatLockedKey(Long showtimeId, Long seatId) {
        return "seat_lock:" + showtimeId + ":" + seatId;
    }

    private List<String> generateListSeatLockedKeys(Long showtimeId, List<Long> seatIds) {
        return seatIds.stream().sorted().map(
                seatId -> generateSeatLockedKey(showtimeId, seatId)
        ).toList();
    }

    public Set<Long> getLockedSeatsByShowtimeId(Long showtimeId) {
        return new HashSet<>(seatHoldService.getActiveSeatIds(showtimeId));
    }

    public boolean areAvailable(List<Long> seatIds, Long showtimeId) {
        return seatRepository.areAvailable(seatIds, showtimeId);
    }

    public List<Seat> fetchSeatsByTicketId(Long ticketId) {
        return seatRepository.findByTicketId(ticketId);
    }

    public List<Seat> fetchSeatsByReservationId(Long reservationId) {
        return seatRepository.findByReservationId(reservationId);
    }


}
