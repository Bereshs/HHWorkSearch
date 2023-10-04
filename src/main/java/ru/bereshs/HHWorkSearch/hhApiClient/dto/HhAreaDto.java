package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import lombok.Data;

@Data
public class HhAreaDto {
    int id;
    String name;
    String url;

    public String toString () {
        return name;
    }
}
