package com.kodilla.springbatch.springbatch;

import com.kodilla.springbatch.model.PersonAge;
import com.kodilla.springbatch.model.PersonBirthDate;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PersonProcessor implements ItemProcessor<PersonBirthDate, PersonAge> {
    @Override
    public PersonAge process(PersonBirthDate item) {
        PersonAge personAge = new PersonAge();
        personAge.setId(item.getId());
        personAge.setName(item.getName());
        personAge.setSurname(item.getSurname());
        personAge.setAge(getAge(item));
        return personAge;
    }

    private int getAge(PersonBirthDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd].[M][MM].yyyy");
        LocalDate birthDate = LocalDate.parse(date.getBirthDate(), formatter);
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthDate, today);
        return age.getYears();
    }
}
