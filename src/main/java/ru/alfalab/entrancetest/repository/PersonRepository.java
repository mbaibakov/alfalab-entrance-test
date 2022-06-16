package ru.alfalab.entrancetest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alfalab.entrancetest.entity.Person;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    @Query("select distinct p from Person p " +
            "join fetch p.documents d " +
            "where d.number like %:number% and " +
            "(d.expirationDate is null or d.expirationDate > CURRENT_DATE())"
    )
    List<Person> findAllByActiveDocumentsNumber(@Param("number") String number);
}
