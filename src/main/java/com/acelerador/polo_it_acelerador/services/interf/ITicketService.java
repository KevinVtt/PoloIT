package com.acelerador.polo_it_acelerador.services.interf;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.acelerador.polo_it_acelerador.models.Ticket;

@Repository
public interface ITicketService {
    List<Ticket> findAll();
    Ticket findById(Long id);
    Ticket save(Ticket ticket);
    Ticket update(Ticket ticket);
    void deleteById(Long id);

}
