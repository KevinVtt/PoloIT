package com.acelerador.polo_it_acelerador.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acelerador.polo_it_acelerador.models.Ticket;
import com.acelerador.polo_it_acelerador.models.dto.request.TicketRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.TicketResponseDTO;
import com.acelerador.polo_it_acelerador.models.mappers.TicketMapper;
import com.acelerador.polo_it_acelerador.services.interf.ITicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final ITicketService ticketService;

    public TicketController(ITicketService ticketService) {
        this.ticketService = ticketService;
    }    

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets(){
        List<TicketResponseDTO> lstTicketsResponse = TicketMapper.toResponseDTOList(ticketService.findAll());
        return ResponseEntity.ok(lstTicketsResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getById(@PathVariable Long id){
        TicketResponseDTO ticketResponse = TicketMapper.toResponseDTO(ticketService.findById(id));
        return ResponseEntity.ok(ticketResponse);
    }

    @PostMapping("/crear")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketDto){
        Ticket ticket = TicketMapper.toEntity(ticketDto);
        Ticket ticketSave = ticketService.save(ticket);
        return ResponseEntity.ok(TicketMapper.toResponseDTO(ticketSave));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable Long id,@RequestBody TicketResponseDTO ticketDto){
        Ticket updatedTicket = validateTicket(id, ticketDto);
        return ResponseEntity.ok(TicketMapper.toResponseDTO(updatedTicket));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id){
        ticketService.deleteById(id);
        return ResponseEntity.ok("Ticket eliminado con exito! ID: " + id);
    }
    
    private Ticket validateTicket(Long id, TicketResponseDTO dto){
        Ticket ticketDb = ticketService.findById(id);
        ticketDb.setEmail(dto.email());
        ticketDb.setDescripcion(dto.descripcion());
        ticketDb.setAsunto(dto.asunto());
        return ticketService.update(ticketDb);
    }

    

}
