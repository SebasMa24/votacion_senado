package com.group1.votacion_senado.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "candidatos", schema = "senado")
public class Candidato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_candidato")
    private int idCandidato;

    @Column(name = "nombre_c", nullable = false)
    private String nombre;

    @Column(name = "apellido_c", nullable = false)
    private String apellido;

    @Column(name = "numero_lista", nullable = false)
    private int numLista;

    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false) 
    @JsonBackReference
    private PartidoPolitico partidoPolitico;


    @Column(name = "total_votos_c", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int totalVotosC = 0; 
}
