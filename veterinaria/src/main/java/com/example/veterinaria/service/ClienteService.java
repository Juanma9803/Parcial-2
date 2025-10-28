package com.example.veterinaria.service;

import com.example.veterinaria.model.Cliente;
import com.example.veterinaria.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public List<Cliente> listarTodos() {
        return clienteRepo.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + id));
    }

    public Cliente obtenerPorDocumento(String documento) {
        Cliente c = clienteRepo.findByDocumento(documento);
        if (c == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con documento: " + documento);
        return c;
    }

    public Cliente crear(Cliente cliente) {
        if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documento es obligatorio");
        }
        // Verificar unicidad documento
        Cliente existente = clienteRepo.findByDocumento(cliente.getDocumento());
        if (existente != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese documento");
        }
        return clienteRepo.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente payload) {
        Cliente existente = clienteRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + id));

        // Si cambia documento, verificar unicidad
        if (payload.getDocumento() != null && !payload.getDocumento().equals(existente.getDocumento())) {
            Cliente porDoc = clienteRepo.findByDocumento(payload.getDocumento());
            if (porDoc != null && !porDoc.getIdCliente().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Documento ya en uso por otro cliente");
            }
            existente.setDocumento(payload.getDocumento());
        }

        if (payload.getNombre() != null) existente.setNombre(payload.getNombre());
        if (payload.getTelefono() != null) existente.setTelefono(payload.getTelefono());
        if (payload.getCorreo() != null) existente.setCorreo(payload.getCorreo());
        if (payload.getDireccion() != null) existente.setDireccion(payload.getDireccion());

        return clienteRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (!clienteRepo.existsById(Math.toIntExact(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + id);
        }
        clienteRepo.deleteById(Math.toIntExact(id));
    }
}

