package com.example.veterinaria.controller;

import com.example.veterinaria.model.Veterinario;
import com.example.veterinaria.service.VeterinarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {
    private final VeterinarioService service;
    public VeterinarioController(VeterinarioService service) { this.service = service; }

    @GetMapping
    public List<Veterinario> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public Veterinario obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public Veterinario crear(@RequestBody Veterinario v) { return service.crear(v); }

    @PutMapping("/{id}")
    public Veterinario actualizar(@PathVariable Long id, @RequestBody Veterinario v) { return service.actualizar(id, v); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}

