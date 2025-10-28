package com.example.veterinaria.model;



import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "veterinario")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVeterinario;

    private String nombre;
    private String especialidad;
    private String registro_profesional;

    public Long getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(Long idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getRegistro_profesional() {
        return registro_profesional;
    }

    public void setRegistro_profesional(String registro_profesional) {
        this.registro_profesional = registro_profesional;
    }
}

