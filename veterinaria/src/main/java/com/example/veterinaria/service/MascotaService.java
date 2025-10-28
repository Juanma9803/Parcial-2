package com.example.veterinaria.service;

import com.example.veterinaria.model.Cliente;
import com.example.veterinaria.model.Mascota;
import com.example.veterinaria.repository.ClienteRepository;
import com.example.veterinaria.repository.MascotaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepo;
    private final ClienteRepository clienteRepo;

    public MascotaService(MascotaRepository mascotaRepo, ClienteRepository clienteRepo) {
        this.mascotaRepo = mascotaRepo;
        this.clienteRepo = clienteRepo;
    }

    public List<Mascota> listarTodos() {
        return mascotaRepo.findAll();
    }

    public Mascota obtenerPorId(Long id) {
        return mascotaRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada con id: " + id));
    }

    public List<Mascota> listarPorDocumentoCliente(String documento) {
        return mascotaRepo.findMascotasPorDocumentoCliente(documento);
    }

    public Mascota crear(Mascota mascota) {
        if (mascota.getCliente() == null || mascota.getCliente().getIdCliente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el id del cliente dueño (cliente.id_cliente)");
        }
        Long clienteId = mascota.getCliente().getIdCliente();
        Cliente cliente = clienteRepo.findById(Math.toIntExact(clienteId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no existe con id: " + clienteId));
        mascota.setCliente(cliente);
        return mascotaRepo.save(mascota);
    }

    public Mascota actualizar(Long id, Mascota payload) {
        Mascota existente = mascotaRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada con id: " + id));

        if (payload.getNombre() != null) existente.setNombre(payload.getNombre());
        if (payload.getEspecie() != null) existente.setEspecie(payload.getEspecie());
        if (payload.getRaza() != null) existente.setRaza(payload.getRaza());
        if (payload.getSexo() != null) existente.setSexo(payload.getSexo());
        if (payload.getFecha_nac() != null) existente.setFecha_nac(payload.getFecha_nac());

        if (payload.getCliente() != null && payload.getCliente().getIdCliente() != null) {
            Long nuevoClienteId = payload.getCliente().getIdCliente();
            Cliente cliente = clienteRepo.findById(Math.toIntExact(nuevoClienteId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no existe con id: " + nuevoClienteId));
            existente.setCliente(cliente);
        }

        return mascotaRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (!mascotaRepo.existsById(Math.toIntExact(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada con id: " + id);
        }
        mascotaRepo.deleteById(Math.toIntExact(id));
    }
}

