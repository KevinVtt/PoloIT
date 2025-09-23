package com.acelerador.polo_it_acelerador;

import com.acelerador.polo_it_acelerador.controllers.TicketController;
import com.acelerador.polo_it_acelerador.models.Ticket;
import com.acelerador.polo_it_acelerador.models.dto.request.TicketRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.TicketResponseDTO;
import com.acelerador.polo_it_acelerador.services.interf.ITicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketTest {

    private ITicketService ticketService;
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        ticketService = Mockito.mock(ITicketService.class);
        ticketController = new TicketController(ticketService);
    }

    @Test
    void testCreateTicket() {
        TicketRequestDTO dto = new TicketRequestDTO("test@mail.com", "Asunto prueba", "Descripcion prueba");

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setEmail(dto.email());
        savedTicket.setAsunto(dto.asunto());
        savedTicket.setDescripcion(dto.descripcion());

        when(ticketService.save(any(Ticket.class))).thenReturn(savedTicket);

        ResponseEntity<TicketResponseDTO> response = ticketController.createTicket(dto);
        System.out.println("Ticket creado: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test@mail.com", response.getBody().email());
        verify(ticketService, times(1)).save(any(Ticket.class));
    }

    @Test
    void testGetAllTickets() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEmail("test@mail.com");
        ticket.setAsunto("Asunto prueba");
        ticket.setDescripcion("Descripcion prueba");

        when(ticketService.findAll()).thenReturn(Arrays.asList(ticket));

        ResponseEntity<List<TicketResponseDTO>> response = ticketController.getAllTickets();
        System.out.println("Tickets obtenidos: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(ticketService, times(1)).findAll();
    }

    @Test
    void testGetTicketById() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEmail("test@mail.com");
        ticket.setAsunto("Asunto prueba");
        ticket.setDescripcion("Descripcion prueba");

        when(ticketService.findById(1L)).thenReturn(ticket);

        ResponseEntity<TicketResponseDTO> response = ticketController.getById(1L);
        System.out.println("Ticket obtenido: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test@mail.com", response.getBody().email());
        verify(ticketService, times(1)).findById(1L);
    }

    @Test
    void testUpdateTicket() {
        TicketResponseDTO dto = new TicketResponseDTO(1L, "update@mail.com", "Asunto actualizado", "Descripcion actualizada");

        Ticket existingTicket = new Ticket();
        existingTicket.setId(1L);
        existingTicket.setEmail("test@mail.com");
        existingTicket.setAsunto("Asunto prueba");
        existingTicket.setDescripcion("Descripcion prueba");

        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(1L);
        updatedTicket.setEmail(dto.email());
        updatedTicket.setAsunto(dto.asunto());
        updatedTicket.setDescripcion(dto.descripcion());

        when(ticketService.findById(1L)).thenReturn(existingTicket);
        when(ticketService.update(any(Ticket.class))).thenReturn(updatedTicket);

        ResponseEntity<TicketResponseDTO> response = ticketController.updateTicket(1L, dto);
        System.out.println("Ticket actualizado: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("update@mail.com", response.getBody().email());
        assertEquals("Asunto actualizado", response.getBody().asunto());
        assertEquals("Descripcion actualizada", response.getBody().descripcion());

        verify(ticketService, times(1)).findById(1L);
        verify(ticketService, times(1)).update(any(Ticket.class));
    }

    @Test
    void testDeleteTicket() {
        doNothing().when(ticketService).deleteById(1L);

        ResponseEntity<String> response = ticketController.deleteTicket(1L);
        System.out.println("Ticket eliminado: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        verify(ticketService, times(1)).deleteById(1L);
    }
}
