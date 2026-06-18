package com.nmquan1503.backend_springboot.mappers.ticket;

import com.nmquan1503.backend_springboot.dtos.responses.ticket.TicketPriceResponse;
import com.nmquan1503.backend_springboot.entities.ticket.TicketPrice;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:58+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TicketPriceMapperImpl implements TicketPriceMapper {

    @Override
    public TicketPriceResponse toTicketPriceResponse(TicketPrice ticketPrice) {
        if ( ticketPrice == null ) {
            return null;
        }

        TicketPriceResponse.TicketPriceResponseBuilder ticketPriceResponse = TicketPriceResponse.builder();

        ticketPriceResponse.id( ticketPrice.getId() );
        ticketPriceResponse.dayOfWeek( ticketPrice.getDayOfWeek() );
        ticketPriceResponse.timeRangeStart( ticketPrice.getTimeRangeStart() );
        ticketPriceResponse.timeRangeEnd( ticketPrice.getTimeRangeEnd() );
        ticketPriceResponse.price( ticketPrice.getPrice() );

        return ticketPriceResponse.build();
    }

    @Override
    public List<TicketPriceResponse> toListTicketPriceResponse(List<TicketPrice> ticketPrices) {
        if ( ticketPrices == null ) {
            return null;
        }

        List<TicketPriceResponse> list = new ArrayList<TicketPriceResponse>( ticketPrices.size() );
        for ( TicketPrice ticketPrice : ticketPrices ) {
            list.add( toTicketPriceResponse( ticketPrice ) );
        }

        return list;
    }
}
