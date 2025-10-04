package com.acelerador.polo_it_acelerador.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acelerador.polo_it_acelerador.models.Contact;
import com.acelerador.polo_it_acelerador.repositories.IContact;
import com.acelerador.polo_it_acelerador.services.interf.IContactService;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    private IContact contactRepository;

    @Override
    public void deleteById(Long id) {
        try{
            contactRepository.deleteById(id);
        }catch(Exception e){
            throw new RuntimeException("Hubo un error inesperado");
        }
    }

    @Override
    public List<Contact> findAll() {
        try{
            return contactRepository.findAll();
        }catch(Exception e){
            throw new RuntimeException("Error al obtener los contactos: " + e.getMessage());
        }
    }

    @Override
    public Contact findByEmail(String email) {
        try{
            return contactRepository.findByEmail(email);
        }catch(Exception e){
            throw new RuntimeException("Error al buscar el contacto por email: " + e.getMessage());
        }
    }

    @Override
    public Contact findById(Long id) {
        try{
            return contactRepository.findById(id).orElse(null);
        }catch(Exception e){
            throw new RuntimeException("Hubo un error al buscar el contacto por id");
        }
    }

    @Override
    public Contact save(Contact contact) {
        try{
            return contactRepository.save(contact);
        }catch(Exception e){
            throw new RuntimeException("Hubo un error al guardar el contacto: " + e.getMessage());
        }
    }

    @Override
    public Contact update(Contact contact) {
        try{
            return contactRepository.save(contact);
        }catch(Exception e){
            throw new RuntimeException("Hubo un error al actualizar el contacto: " + e.getMessage());
        }
    }
    
}
