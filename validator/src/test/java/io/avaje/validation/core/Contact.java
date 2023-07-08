package io.avaje.validation.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//@Valid
public class Contact {
    @NotBlank(groups = BasicTest.class)
    public String firstName;
    @Size(max = 5)
    public String lastName;

    public Address address;

    public Contact() {
        this.firstName = "fn";
        this.lastName = "ln";
    }
    public Contact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
