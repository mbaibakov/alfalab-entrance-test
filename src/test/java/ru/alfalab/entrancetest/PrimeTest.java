package ru.alfalab.entrancetest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.nio.file.StandardOpenOption.APPEND;

@Slf4j
public class PrimeTest {
    private static final String RESULT_FILE_NAME = "Result.txt";
    private static final String FIRST_FILE_NAME = "Thread1.txt";
    private static final String SECOND_FILE_NAME = "Thread2.txt";

    private static final long PRIME_MAX_NUMBER = 1_000_000;
    private static final long BASIC_PRIME_MAX_NUMBER = getBasicPrimeMaxNumber();
    private static final List<Long> BASIC_PRIME = getBasicPrime();

    private AtomicLong currentNumber = new AtomicLong(BASIC_PRIME_MAX_NUMBER);

    @Test
    @DisplayName("Задача 3")
    public void writePrimes() throws IOException, InterruptedException {
        //Отчищаем файлы и записываем базовые простые
        clearFiles();
        writeBasicPrime();
        //Запускаем последовательную запись
        long serialStartTime = System.currentTimeMillis();
        for (long i = BASIC_PRIME_MAX_NUMBER; i < PRIME_MAX_NUMBER; i += 2) {
            if (isPrime(i)) {
                writeToResulAndThreadFile(FIRST_FILE_NAME, i);
            }
        }
        long serialEndTime = System.currentTimeMillis();
        long serialTime = serialEndTime - serialStartTime;
        log.info("Последовательная запиcь заняла {} миллисекунд", serialTime);

        //Отчищаем файлы и записываем базовые простые
        clearFiles();
        writeBasicPrime();
        //Запускаем параллельную запись
        long parallelStartTime = System.currentTimeMillis();
        PrimeWriter firstThread = new PrimeWriter(FIRST_FILE_NAME);
        PrimeWriter secondThread = new PrimeWriter(SECOND_FILE_NAME);

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();
        long parallelEndTime = System.currentTimeMillis();
        long parallelTime = parallelEndTime - parallelStartTime;
        log.info("Параллельная запиcь заняла {} миллисекунд", parallelTime);

        Assertions.assertTrue(parallelTime < serialTime);
    }

    class PrimeWriter extends Thread {

        private final String fileName;

        PrimeWriter(String fileName) {
            this.fileName = fileName;
        }

        @SneakyThrows
        @Override
        public void run() {
            long primeCandidate = getPrimeCandidate();
            while (primeCandidate < PRIME_MAX_NUMBER) {
                if (isPrime(primeCandidate)) {
                    writeToResulAndThreadFile(fileName, primeCandidate);
                }
                primeCandidate = getPrimeCandidate();
            }
        }

        private long getPrimeCandidate() {
            return currentNumber.addAndGet(2);
        }
    }

    private static Long getBasicPrimeMaxNumber() {
        long round = round(sqrt(PRIME_MAX_NUMBER));
        return round % 2 == 0 ? round + 1 : round;
    }

    private static List<Long> getBasicPrime() {
        List<Long> primes = new LinkedList<>();
        primes.add(2L);
        for (long i = 3; i <= BASIC_PRIME_MAX_NUMBER; i += 2) {
            if (isPrime(i, primes)) {
                primes.add(i);
            }
        }
        return primes;
    }

    private static boolean isPrime(Long i, List<Long> primes) {
        for (Long prime : primes) {
            if (i % prime == 0) return false;
        }
        return true;
    }

    private static boolean isPrime(Long i) {
        return isPrime(i, BASIC_PRIME);
    }

    private void clearFiles() throws IOException {
        replaceFile(RESULT_FILE_NAME);
        replaceFile(FIRST_FILE_NAME);
        replaceFile(SECOND_FILE_NAME);
    }

    private void replaceFile(String fileName) throws IOException {
        Path path = Path.of(fileName);
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    private void writeBasicPrime() throws IOException {
        for (Long i : BASIC_PRIME) {
            writeToFile(RESULT_FILE_NAME, i);
        }
    }

    private void writeToFile(String fileName, Long i) throws IOException {
        Files.write(Path.of(fileName), (i + " ").getBytes(StandardCharsets.UTF_8), APPEND);
    }

    private void writeToResulAndThreadFile(String threadFileName, Long i) throws IOException {
        writeToFile(RESULT_FILE_NAME, i);
        writeToFile(threadFileName, i);
    }
}
