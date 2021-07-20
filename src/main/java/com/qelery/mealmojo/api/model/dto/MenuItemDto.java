package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class MenuItemDto {

    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
}
