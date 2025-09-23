package com.acelerador.polo_it_acelerador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acelerador.polo_it_acelerador.models.Ticket;

public interface ITicket extends JpaRepository<Ticket,Long> {

}
