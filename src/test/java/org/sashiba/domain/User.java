package org.sashiba.domain;

import net.datafaker.Faker;

record Address(String street, String city, String state, String country, String postal_code) {
}

public record User(String first_name,
                   String last_name,
                   Address address,
                   String phone,
                   String dob,
                   String password,
                   String email) {
    public static User randomUser() {
        Faker faker = new Faker();
        Address address = new Address(faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().country(),
                faker.address().postcode());

        return new User(
                faker.name().firstName(),
                faker.name().lastName(),
                address,
                faker.phoneNumber().phoneNumber(),
                "1990-01-01",
                "Az123!xyz",
                faker.internet().emailAddress()
        );
    }

    public User withPassword(String password) {
        return new User(first_name, last_name, address, phone, dob, password, email);
    }

    public User withFirstName(String firstName) {
        return new User(firstName, last_name, address, phone, dob, password, email);
    }

    public User withLastName(String lastName) {
        return new User(first_name, lastName, address, phone, dob, password, email);
    }
}
