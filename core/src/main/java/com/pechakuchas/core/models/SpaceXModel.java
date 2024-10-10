package com.pechakuchas.core.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceXModel {
    private String name;
    private String dateLocal;
    private Boolean success;
    private String details;
}
