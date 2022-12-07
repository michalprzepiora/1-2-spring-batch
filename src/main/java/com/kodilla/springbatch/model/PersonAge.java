package com.kodilla.springbatch.model;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PersonAge extends Person{
    private int age;
}
