package com.example.veterinaria.service;

import com.example.veterinaria.model.Cita;
import com.example.veterinaria.model.Mascota;
import com.example.veterinaria.model.Veterinario;
import com.example.veterinaria.repository.CitaRepository;
import com.example.veterinaria.repository.MascotaRepository;
import com.example.veterinaria.repository.VeterinarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepo;
    private final MascotaRepository mascotaRepo;
    private final VeterinarioRepository vetRepo;

    public CitaService(CitaRepository citaRepo, MascotaRepository mascotaRepo, VeterinarioRepository vetRepo) {
        this.citaRepo = citaRepo;
        this.mascotaRepo = mascotaRepo;
        this.vetRepo = vetRepo;
    }

    public List<Cita> listarTodos() {
        return citaRepo.findAll();
    }

    public Cita obtenerPorId(Long id) {
        return citaRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada con id: " + id));
    }

    @Transactional
    public Cita crear(Cita cita) {
        // validaciones
        if (cita.getFecha_hora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecha_hora es obligatoria");
        }
        if (cita.getFecha_hora().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha/hora de la cita no puede estar en el pasado");
        }
        if (cita.getMascota() == null || cita.getMascota().getIdMascota () == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar mascota.id_mascota");
        }
        if (cita.getVeterinario() == null || cita.getVeterinario().getIdVeterinario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar veterinario.id_veterinario");
        }

        // verificar existencia FK
        Long idMascota = cita.getMascota().getIdMascota();
        Mascota mascota = mascotaRepo.findById(Math.toIntExact(idMascota))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mascota no existe con id: " + idMascota));
        Long idVet = cita.getVeterinario().getIdVeterinario();
        Veterinario vet = vetRepo.findById(Math.toIntExact(idVet))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veterinario no existe con id: " + idVet));

        cita.setMascota(mascota);
        cita.setVeterinario(vet);

        if (cita.getEstado() == null || cita.getEstado().isBlank()) {
            cita.setEstado("Programada");
        } else {
            String est = cita.getEstado();
            if (!est.equalsIgnoreCase("Programada") && !est.equalsIgnoreCase("Atendida") && !est.equalsIgnoreCase("Cancelada")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido. Valores permitidos: Programada, Atendida, Cancelada");
            }
        }

        return citaRepo.save(cita);
    }

    @Transactional
    public Cita actualizar(Long id, Cita payload) {
        Cita existente = citaRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada con id: " + id));

        if (payload.getFecha_hora() != null) {
            if (payload.getFecha_hora().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva fecha no puede estar en el pasado");
            }
            existente.setFecha_hora(payload.getFecha_hora());
        }

        if (payload.getMotivo() != null) existente.setMotivo(payload.getMotivo());

        if (payload.getEstado() != null) {
            String est = payload.getEstado();
            if (!est.equalsIgnoreCase("Programada") && !est.equalsIgnoreCase("Atendida") && !est.equalsIgnoreCase("Cancelada")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido. Valores permitidos: Programada, Atendida, Cancelada");
            }
            existente.setEstado(est);
        }

        if (payload.getMascota() != null && payload.getMascota().getIdMascota() != null) {
            Long idMasc = payload.getMascota().getIdMascota();
            Mascota m = mascotaRepo.findById(Math.toIntExact(idMasc))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mascota no existe con id: " + idMasc));
            existente.setMascota(m);
        }

        if (payload.getVeterinario() != null && payload.getVeterinario().getIdVeterinario() != null) {
            Long idV = payload.getVeterinario().getIdVeterinario();
            Veterinario v = vetRepo.findById(Math.toIntExact(idV))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veterinario no existe con id: " + idV));
            existente.setVeterinario(v);
        }

        return citaRepo.save(existente);
    }

    public void eliminar(Long id) {
        if (!citaRepo.existsById(Math.toIntExact(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada con id: " + id);
        }
        citaRepo.deleteById(Math.toIntExact(id));
    }

    // --- Métodos que usan consultas nativas (repositorio debe implementarlos) ---

    /** 1) Mascotas por documento de cliente (retorna lista de Mascota) */
    public List<Mascota> mascotasPorDocumentoCliente(String documento) {
        return mascotaRepo.findMascotasPorDocumentoCliente(documento);
    }

    /** 2) Citas pendientes de un veterinario desde la fecha/hora actual */
    public List<Cita> citasPendientesPorVeterinario(Long idVeterinario, LocalDateTime desde) {
        // assume citaRepo.findCitasPendientesPorVeterinario existe y acepta (Long, LocalDateTime)
        return citaRepo.findCitasPendientesPorVeterinario(Math.toIntExact(idVeterinario), desde);
    }

    /** 3) Total mascotas por especie -> mapea List<Object[]> -> DTO simple */
    public List<SpeciesCount> contarMascotasPorEspecie() {
        List<Object[]> rows = citaRepo.contarMascotasPorEspecie();
        List<SpeciesCount> out = new ArrayList<>();
        for (Object[] r : rows) {
            String especie = r[0] == null ? null : r[0].toString();
            Long total = r[1] == null ? 0L : ((Number) r[1]).longValue();
            out.add(new SpeciesCount(especie, total));
        }
        return out;
    }

    /** 4) Historial de citas de una mascota con detalles */
    public List<AppointmentHistory> historialCitasPorMascota(Long idMascota) {
        List<Object[]> rows = citaRepo.findCitasPorMascota(Math.toIntExact(idMascota));
        List<AppointmentHistory> out = new ArrayList<>();
        for (Object[] r : rows) {
            // r: fecha_hora, motivo, estado, veterinario
            AppointmentHistory h = new AppointmentHistory();
            h.setFecha_hora(r[0] == null ? null : r[0].toString());
            h.setMotivo(r[1] == null ? null : r[1].toString());
            h.setEstado(r[2] == null ? null : r[2].toString());
            h.setVeterinario(r[3] == null ? null : r[3].toString());
            out.add(h);
        }
        return out;
    }

    /** 5) Veterinarios con más citas (retorna nombre + total) */
    public List<VetCount> veterianriosConMasCitas() {
        List<Object[]> rows = citaRepo.findVeterinariosConMasCitas();
        List<VetCount> out = new ArrayList<>();
        for (Object[] r : rows) {
            String nombre = r[0] == null ? null : r[0].toString();
            Long total = r[1] == null ? 0L : ((Number) r[1]).longValue();
            out.add(new VetCount(nombre, total));
        }
        return out;
    }

    // --- DTOs internos simples ---
    public static class SpeciesCount {
        private String especie;
        private Long total;

        public SpeciesCount() {}
        public SpeciesCount(String especie, Long total) { this.especie = especie; this.total = total; }

        public String getEspecie() { return especie; }
        public void setEspecie(String especie) { this.especie = especie; }
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
    }

    public static class AppointmentHistory {
        private String fecha_hora;
        private String motivo;
        private String estado;
        private String veterinario;

        public String getFecha_hora() { return fecha_hora; }
        public void setFecha_hora(String fecha_hora) { this.fecha_hora = fecha_hora; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getVeterinario() { return veterinario; }
        public void setVeterinario(String veterinario) { this.veterinario = veterinario; }
    }

    public static class VetCount {
        private String nombre;
        private Long total;

        public VetCount() {}
        public VetCount(String nombre, Long total) { this.nombre = nombre; this.total = total; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
    }
}

