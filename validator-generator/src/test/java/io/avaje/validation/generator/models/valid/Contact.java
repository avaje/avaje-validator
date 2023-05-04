package io.avaje.validation.generator.models.valid;

import io.avaje.validation.ValidPojo;

@ValidPojo
public class Contact {
    public String firstName;
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
