package com.example.veterinaria.repository;


import com.example.veterinaria.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Consultas nativas que pide el taller
    @Query(value = "SELECT * FROM cita WHERE id_veterinario = ?1 AND fecha_hora >= ?2 AND estado = 'Programada'", nativeQuery = true)
    List<Cita> findCitasPendientesPorVeterinario(int idVeterinario, LocalDateTime fechaActual);

    @Query(value = "SELECT especie, COUNT(*) AS total FROM mascota GROUP BY especie", nativeQuery = true)
    List<Object[]> contarMascotasPorEspecie();

    @Query(value = """
                SELECT c.fecha_hora, c.motivo, c.estado, v.nombre AS veterinario
                FROM cita c
                INNER JOIN veterinario v ON c.id_veterinario = v.id_veterinario
                WHERE c.id_mascota = ?1
            """, nativeQuery = true)
    List<Object[]> findCitasPorMascota(int idMascota);

    @Query(value = """
                SELECT v.nombre, COUNT(c.id_cita) AS total_citas
                FROM cita c
                INNER JOIN veterinario v ON c.id_veterinario = v.id_veterinario
                WHERE c.estado = 'Atendida'
                GROUP BY v.id_veterinario
                ORDER BY total_citas DESC
            """, nativeQuery = true)
    List<Object[]> findVeterinariosConMasCitas();
}


