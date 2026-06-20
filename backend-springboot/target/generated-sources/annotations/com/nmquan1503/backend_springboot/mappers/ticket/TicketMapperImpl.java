package com.nmquan1503.backend_springboot.mappers.ticket;

import com.nmquan1503.backend_springboot.dtos.responses.ticket.TicketDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.ticket.TicketStatusResponse;
import com.nmquan1503.backend_springboot.entities.ticket.Ticket;
import com.nmquan1503.backend_springboot.entities.ticket.TicketStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:50+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TicketMapperImpl implements TicketMapper {

    @Override
    public TicketDetailResponse toTicketDetailResponse(Ticket ticket) {
        if ( ticket == null ) {
            return null;
        }

        TicketDetailResponse.TicketDetailResponseBuilder ticketDetailResponse = TicketDetailResponse.builder();

        ticketDetailResponse.id( ticket.getId() );
        ticketDetailResponse.status( ticketStatusToTicketStatusResponse( ticket.getStatus() ) );

        return ticketDetailResponse.build();
    }

    protected TicketStatusResponse ticketStatusToTicketStatusResponse(TicketStatus ticketStatus) {
        if ( ticketStatus == null ) {
            return null;
        }

        TicketStatusResponse.TicketStatusResponseBuilder ticketStatusResponse = TicketStatusResponse.builder();

        ticketStatusResponse.id( ticketStatus.getId() );
        ticketStatusResponse.name( ticketStatus.getName() );

        return ticketStatusResponse.build();
    }
}
