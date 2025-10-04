package com.acelerador.polo_it_acelerador.services.interf;

import java.util.List;

import com.acelerador.polo_it_acelerador.models.Contact;

public interface IContactService {
    List<Contact> findAll();
    Contact findById(Long id);
    Contact save(Contact contact);
    Contact update(Contact contact);
    void deleteById(Long id);
    Contact findByEmail(String email);
}
