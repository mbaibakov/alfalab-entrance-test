package ru.alfalab.entrancetest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DOCUMENT")
public class Document {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "NUMBER", nullable = false)
    private String number;

    @Column(name = "SERIES")
    private String series;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "ISSUER")
    private String issuer;

    @Column(name = "EXPIRATION_DATE")
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID", nullable = false, updatable = false)
    private Person person;
}
