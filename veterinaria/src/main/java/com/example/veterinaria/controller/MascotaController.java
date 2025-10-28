package com.example.veterinaria.controller;

import com.example.veterinaria.model.Mascota;
import com.example.veterinaria.service.MascotaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {
    private final MascotaService service;
    public MascotaController(MascotaService service) { this.service = service; }

    @GetMapping
    public List<Mascota> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public Mascota obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @GetMapping("/cliente")
    public List<Mascota> porDocumento(@RequestParam String documento) { return service.listarPorDocumentoCliente(documento); }

    @PostMapping
    public Mascota crear(@RequestBody Mascota m) { return service.crear(m); }

    @PutMapping("/{id}")
    public Mascota actualizar(@PathVariable Long id, @RequestBody Mascota m) { return service.actualizar(id, m); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}

