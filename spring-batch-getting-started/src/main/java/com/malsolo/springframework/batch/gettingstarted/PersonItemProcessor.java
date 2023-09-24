package com.malsolo.springframework.batch.gettingstarted;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(final Person person) throws Exception {
        var transformedPerson = new Person(person.firstName().toUpperCase(), person.lastName().toUpperCase());

        log.info("Converted person {} into {}", person, transformedPerson);

        return transformedPerson;
    }
}
