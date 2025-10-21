package com.group1.votacion_senado.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "candidato", schema = "votacion_senado")
public class Candidato {
    @Id
    @Column(name = "id_candidato")
    private int idCandidato;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "num_lista", nullable = false)
    private int numLista;

    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false) 
    @JsonBackReference
    private PartidoPolitico partidoPolitico;
}
