package com.example.veterinaria.repository;

import com.example.veterinaria.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    @Query(value = """
            SELECT m.*
            FROM mascota m
            INNER JOIN cliente c ON m.id_cliente = c.id_cliente
            WHERE c.numero_documento = ?1
            """, nativeQuery = true)
    List<Mascota> findMascotasPorDocumentoCliente(String numeroDocumento);
}


