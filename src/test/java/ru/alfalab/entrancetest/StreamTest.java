package ru.alfalab.entrancetest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class StreamTest {

    public interface NamedObject {
        String getName();
    }

    class Grouper {

        static <E extends NamedObject> Map<String, List<E>> groupByName(Collection<E> collection) {
            return collection.stream().collect(Collectors.groupingBy(it -> it.getName()));
        }

    }

    @Getter
    @AllArgsConstructor
    @ToString
    class NamedObjectImpl implements NamedObject {
        private final String name;
    }

    @Test
    @DisplayName("Задача 2")
    void test() {
        NamedObject aa = new NamedObjectImpl("aa");
        NamedObject bb = new NamedObjectImpl("bb");
        NamedObject cc = new NamedObjectImpl("cc");

        Map<String, List<NamedObject>> result = Grouper.groupByName(List.of(aa, bb, aa, aa, bb, cc));
        log.info("Результат группировки: {}", result);
        assertTrue(result.containsKey(aa.getName()));
        assertTrue(result.containsKey(bb.getName()));
        assertTrue(result.containsKey(cc.getName()));

        assertEquals(result.get(aa.getName()).size(), 3);
        assertEquals(result.get(bb.getName()).size(), 2);
        assertEquals(result.get(cc.getName()).size(), 1);
    }
}
