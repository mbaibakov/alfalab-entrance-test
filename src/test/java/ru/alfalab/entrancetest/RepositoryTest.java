package ru.alfalab.entrancetest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alfalab.entrancetest.entity.Document;
import ru.alfalab.entrancetest.entity.Person;
import ru.alfalab.entrancetest.repository.DocumentRepository;
import ru.alfalab.entrancetest.repository.PersonRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class RepositoryTest {

    public static final String NUMBER = "777";
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @BeforeAll
    void init(){
        documentRepository.findAll();
        personRepository.deleteAll();

        Person ivan = Person.builder()
                .firstName("Иван")
                .middleName("Иванович")
                .lastName("Иванов")
                .birthDay(LocalDate.of(1989, 01, 21))
                .build();
        ivan = personRepository.save(ivan);

        //Подходящий документ
        Document passport = Document.builder()
                .type("PASSPORT")
                .number("123777")
                .series("1234")
                .issueDate(LocalDate.of(2020, 05, 21))
                .person(ivan)
                .build();

        //Просроченный документ
        Document driverLicence = Document.builder()
                .type("DRIVER_LICENCE")
                .number("7770012")
                .issueDate(LocalDate.of(2009, 03,03))
                .expirationDate(LocalDate.of(2019, 03, 03))
                .person(ivan)
                .build();

        //Документ без 777 в номер
        Document snils = Document.builder()
                .type("SNILS")
                .number("1234567")
                .person(ivan)
                .build();

        //Подходящий документ
        Document inn = Document.builder()
                .type("INN")
                .number("111777222")
                .person(ivan)
                .build();
        documentRepository.saveAll(List.of(passport, driverLicence, snils, inn));

        Person petr = Person.builder()
                .firstName("Петр")
                .lastName("Петров")
                .middleName("Петрович")
                .birthDay(LocalDate.of(2000, 05, 13))
                .build();
        petr = personRepository.save(petr);

        //Документ без 777 в номер
        passport = Document.builder()
                .type("PASSPORT")
                .number("123456")
                .series("1234")
                .issueDate(LocalDate.of(2010, 10, 10))
                .person(petr)
                .build();
        documentRepository.save(passport);
    }

    @Test
    @DisplayName("Задача 1. Через поиск персон по номеру документа")
    public void findPersonByActiveDocumentsNumberTest(){
        List<Person> persons = personRepository.findAllByActiveDocumentsNumber(NUMBER);
        assertEquals(persons.size(), 1);
        Person person = persons.get(0);
        assertEquals(person.getDocuments().size(), 2);
        person.getDocuments().forEach(it -> assertTrue(it.getNumber().contains(NUMBER)
                && (it.getExpirationDate() == null || it.getExpirationDate().isBefore(LocalDate.now())) ));

        person.getDocuments().forEach(d -> print(d, person));

    }

    @Test
    @DisplayName("Задача 2. Через поиск документов")
    public void findActiveDocumentsByNumber() {
        List<Document> documents = documentRepository.findAllActiveByNumber(NUMBER);
        assertEquals(documents.size(), 2);
        documents.forEach(it -> assertTrue(it.getNumber().contains(NUMBER)
                && (it.getExpirationDate() == null || it.getExpirationDate().isBefore(LocalDate.now())) ));

        documents.forEach(d -> print(d, d.getPerson()));
    }

    private void print(Document d, Person p) {
        log.info("ФИО: {} {} {}, Тип: {}, Номер: {}", p.getLastName(), p.getFirstName(), p.getMiddleName(), d.getType(), d.getNumber());
    }


}
