package com.acelerador.polo_it_acelerador.models.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.acelerador.polo_it_acelerador.models.Ticket;
import com.acelerador.polo_it_acelerador.models.dto.request.TicketRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.TicketResponseDTO;

public class TicketMapper {

    public static Ticket toEntity(TicketRequestDTO dto) {
    Ticket ticket = new Ticket();
        ticket.setEmail(dto.email());
        ticket.setAsunto(dto.asunto());
        ticket.setDescripcion(dto.descripcion());
        return ticket;
    }

    public static TicketResponseDTO toResponseDTO(Ticket ticket) {
        return new TicketResponseDTO(
            ticket.getId(),
            ticket.getEmail(),
            ticket.getAsunto(),
            ticket.getDescripcion()
        );
    }

    // Convierte una lista de ticket a dto
    public static List<TicketResponseDTO> toResponseDTOList(List<Ticket> tickets) {
        return tickets.stream()
        .map(TicketMapper::toResponseDTO)
        .collect(Collectors.toList());
    }
    
    // Convierte una lista de dto a ticket
    public static List<Ticket> toEntityList(List<TicketRequestDTO> ticketDtos) {
        return ticketDtos.stream()
                .map(TicketMapper::toEntity)
                .collect(Collectors.toList());
    }
}