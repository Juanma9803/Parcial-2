package com.example.veterinaria.service;

import com.example.veterinaria.model.Veterinario;
import com.example.veterinaria.repository.VeterinarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VeterinarioService {

    private final VeterinarioRepository vetRepo;

    public VeterinarioService(VeterinarioRepository vetRepo) {
        this.vetRepo = vetRepo;
    }

    public List<Veterinario> listarTodos() {
        return vetRepo.findAll();
    }

    public Veterinario obtenerPorId(Long id) {
        return vetRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinario no encontrado con id: " + id));
    }

    public Veterinario crear(Veterinario vet) {
        if (vet.getRegistro_profesional() == null || vet.getRegistro_profesional().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro profesional es obligatorio");
        }
        // opcional: verificar unicidad de registro_profesional si el repo tiene método
        return vetRepo.save(vet);
    }

    public Veterinario actualizar(Long id, Veterinario payload) {
        Veterinario existente = vetRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinario no encontrado con id: " + id));

        if (payload.getNombre() != null) existente.setNombre(payload.getNombre());
        if (payload.getEspecialidad() != null) existente.setEspecialidad(payload.getEspecialidad());
        if (payload.getRegistro_profesional() != null) existente.setRegistro_profesional(payload.getRegistro_profesional());

        return vetRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (!vetRepo.existsById(Math.toIntExact(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinario no encontrado con id: " + id);
        }
        vetRepo.deleteById(Math.toIntExact(id));
    }
}
