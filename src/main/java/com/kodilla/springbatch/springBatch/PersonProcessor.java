package com.kodilla.springbatch.springBatch;

import com.kodilla.springbatch.model.PersonAge;
import com.kodilla.springbatch.model.PersonBirthDate;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class PersonProcessor implements ItemProcessor<PersonBirthDate, PersonAge> {
    @Override
    public PersonAge process(PersonBirthDate item) throws Exception {
        PersonAge personAge = new PersonAge();
        personAge.setId(item.getId());
        personAge.setName(item.getName());
        personAge.setSurname(item.getSurname());
        personAge.setAge(getAge(item));
        return personAge;
    }

    private int getAge(PersonBirthDate date) {
        String dateLine = date.getBirthDate();
        String[] dateArray = dateLine.split("\\.");
        int year = Integer.parseInt(dateArray[2]);
        return LocalDate.now().getYear() - year;
    }
}
