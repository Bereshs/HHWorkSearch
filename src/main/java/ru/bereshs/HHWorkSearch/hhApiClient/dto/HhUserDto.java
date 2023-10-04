package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class HhUserDto {
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String email;
    private String id;

    public void set(HashMap<String, ?> list) {
        setFirstName(list.get("first_name").toString());
        setLastName(list.get("last_name").toString());
        setMiddleName(list.get("middle_name") != null ? list.get("middle_name").toString():null);
        setPhone(list.get("phone").toString());
        setEmail(list.get("email").toString());
        setId(list.get("id").toString());
    }
}
