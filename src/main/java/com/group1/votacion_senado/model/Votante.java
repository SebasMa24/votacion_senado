package com.group1.votacion_senado.model;

import java.text.Normalizer;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "votante", schema = "votacion_senado")
public class Votante implements UserDetails {
    @Id
    @Column(name = "id_votante")
    private int idVotante;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "contraseña", nullable = false)
    private String contraseña;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "ha_votado", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private boolean haVotado;

    @Column(name = "tipo_circunscripcion", nullable = false)
    @Enumerated(EnumType.STRING)
    private Circunscripcion tipoCircunscripcion;

    public Votante(int id_votante, String nombre, String apellido, String correo, String contraseña,
            Circunscripcion tipoCircunscripcion) {
        this.idVotante = id_votante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseña = contraseña;
        this.tipoCircunscripcion = tipoCircunscripcion;
        this.haVotado = false;
        this.username = generarUsername(nombre, apellido, id_votante);
    }

    private String generarUsername(String nombre, String apellido, int id) {
        String primeraLetra = nombre.substring(0, 1).toLowerCase();
        String parteApellido = normalizarTexto(apellido);

        String ultimosDigitos = String.valueOf(id);
        if (ultimosDigitos.length() > 3) {
            ultimosDigitos = ultimosDigitos.substring(ultimosDigitos.length() - 3);
        }

        return primeraLetra + parteApellido + ultimosDigitos;
    }

    private String normalizarTexto(String texto) {
    String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    return sinAcentos.replaceAll("[^a-zA-Z]", "").toLowerCase();
}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipoCircunscripcion.name()));
    }

    @Override
    public String getPassword() {
        return contraseña;
    }

}
