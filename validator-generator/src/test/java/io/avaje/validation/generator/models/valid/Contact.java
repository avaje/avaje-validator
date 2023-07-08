package io.avaje.validation.generator.models.valid;

import java.util.Optional;

import io.avaje.validation.constraints.Valid;

@Valid
public class Contact {
    public String firstName;
    public String lastName;

    public Optional<Address> address;

    public Contact() {
        this.firstName = "fn";
        this.lastName = "ln";
    }
    public Contact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
