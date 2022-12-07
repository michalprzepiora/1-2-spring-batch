package com.kodilla.springbatch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Person {
    private int id;
    private String name;
    private String surname;
}
