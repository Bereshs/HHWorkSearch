package ru.bereshs.hhworksearch.hhapiclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HhSalaryDto {
    int from;
    int to;
    String currency;
    boolean gross;

    public String toString() {
        if(from==0 && to==0) {
            return "Не указана";
        }
        return getK(from) + "-" + getK(to) + " " + currency;
    }

    private String getK(int sum) {
        if (sum > 1000) {
            return String.valueOf(sum / 1000) + "k";
        }
        if(sum==0) {
            return "";
        }
        return String.valueOf(sum);
    }
}
