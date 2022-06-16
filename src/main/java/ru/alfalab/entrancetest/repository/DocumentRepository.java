package ru.alfalab.entrancetest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alfalab.entrancetest.entity.Document;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    @Query("select d from Document d " +
            "join fetch d.person " +
            "where d.number like %:number% and " +
            "(d.expirationDate is null or d.expirationDate > CURRENT_DATE())")
    List<Document> findAllActiveByNumber(@Param("number") String number);
}
