package com.group1.votacion_senado.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "partidospoliticos", schema = "senado")
public class PartidoPolitico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private int idPartido;

    @Column(name = "nom_partido", nullable = false, unique = true)
    private String nomPartido;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "tipo_lista", nullable = false)
    @Enumerated(EnumType.STRING)
    private Lista tipoLista;

    @Column(name = "tipo_circunscripcion_p", nullable = false)
    @Enumerated(EnumType.STRING)
    private Circunscripcion tipoCircunscripcionP;

    @OneToMany(mappedBy = "partidoPolitico", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Candidato> candidatos;

    
    @Column(name = "total_votos_p", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int totalVotosP = 0;

    @Override
    public String toString() {
        return "PartidoPolitico{" +
                "id=" + idPartido +
                ", nombre='" + nomPartido + '\'' +
                ", tipo_lista='" + tipoLista + '\'' +
                ", tipo_circunscripcion='" + tipoCircunscripcionP + '\'' +
                ", total_votos_p=" + totalVotosP +
                ", candidatos=" + (candidatos != null ? candidatos.stream().map(Candidato::getNombre).toList() : "[]") +
                '}';
    }
}
