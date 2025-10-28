package com.example.veterinaria.controller;

import com.example.veterinaria.model.Cita;
import com.example.veterinaria.service.CitaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    private final CitaService service;
    public CitaController(CitaService service) { this.service = service; }

    @GetMapping
    public List<Cita> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public Cita obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public Cita crear(@RequestBody Cita c) { return service.crear(c); }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id, @RequestBody Cita c) { return service.actualizar(id, c); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }

    // Endpoints que exponen consultas nativas
    @GetMapping("/pendientes/veterinario/{idVeterinario}")
    public List<Cita> pendientesVeterinario(@PathVariable Long idVeterinario, @RequestParam(required = false) String desde) {
        LocalDateTime d = (desde == null) ? LocalDateTime.now() : LocalDateTime.parse(desde);
        return service.citasPendientesPorVeterinario(idVeterinario, d);
    }

    @GetMapping("/mascota/{idMascota}/historial")
    public List<CitaService.AppointmentHistory> historialMascota(@PathVariable Long idMascota) {
        return service.historialCitasPorMascota(idMascota);
    }

    @GetMapping("/reportes/mascotas-por-especie")
    public List<CitaService.SpeciesCount> mascotasPorEspecie() {
        return service.contarMascotasPorEspecie();
    }

    @GetMapping("/reportes/veterinarios-top")
    public List<CitaService.VetCount> veterinariosTop() {
        return service.veterianriosConMasCitas();
    }
}

