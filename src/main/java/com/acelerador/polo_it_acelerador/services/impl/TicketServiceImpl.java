package com.acelerador.polo_it_acelerador.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acelerador.polo_it_acelerador.exceptions.ticket.TicketErrorException;
import com.acelerador.polo_it_acelerador.exceptions.ticket.TicketNotFoundException;
import com.acelerador.polo_it_acelerador.models.Ticket;
import com.acelerador.polo_it_acelerador.repositories.ITicket;
import com.acelerador.polo_it_acelerador.services.interf.ITicketService;

@Service
public class TicketServiceImpl implements ITicketService {

    private final ITicket ticketRepository;

    public TicketServiceImpl(ITicket ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteById(Long id) {
        try{
            if(findById(id) != null){
                ticketRepository.deleteById(id);
            }
        }catch(TicketErrorException e){
            throw new TicketErrorException("Hubo un error inesperado");
        }
    }
    
    @Override
    @Transactional
    public List<Ticket> findAll() {
        try{
            return ticketRepository.findAll();
        }catch(TicketErrorException e){
            throw new TicketErrorException("Error al listar los tickets");
        }
    }
    
    @Transactional
    @Override
    public Ticket findById(Long id) {
        try{
            return ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException("No se ha encontrado el ticket con id " + id));
        }catch(TicketErrorException e){
            throw new TicketErrorException("Hubo un error al buscar el ticket por id");
        }
    }
    
    @Override
    @Transactional(readOnly = false)
    public Ticket save(Ticket ticket) {
        try{
            return ticketRepository.save(ticket);
        }catch(TicketErrorException e){
            throw new TicketErrorException("Hubo un error al insertar el ticket");
        }
    }
    
    @Override
    @Transactional(readOnly = false)
    public Ticket update(Ticket ticket) {
        try{
            return ticketRepository.save(ticket);
        }catch(TicketErrorException e){
            throw new TicketErrorException("Hubo un error al actualizar el ticket");
        }
    }
    
}
