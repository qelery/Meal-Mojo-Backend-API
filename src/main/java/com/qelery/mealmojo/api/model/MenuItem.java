package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private String category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_profile_id")
    private MerchantProfile merchantProfile;

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", available=" + available +
                ", category='" + category + '\'' +
                ", merchantProfile=" + merchantProfile +
                '}';
    }
}
