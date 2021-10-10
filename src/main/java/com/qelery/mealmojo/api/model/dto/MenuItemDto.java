package com.qelery.mealmojo.api.model.dto;

import lombok.Data;

@Data
public class MenuItemDto {

    private Long id;
    private String name;
    private String description;
    private Long price; // cents
    private String imageUrl;
    private Boolean isAvailable;
}
