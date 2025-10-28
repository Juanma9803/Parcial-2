package com.example.veterinaria.controller;

import com.example.veterinaria.model.Cliente;
import com.example.veterinaria.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    @GetMapping
    public List<Cliente> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public Cliente crear(@RequestBody Cliente c) { return service.crear(c); }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @RequestBody Cliente c) { return service.actualizar(id, c); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}

