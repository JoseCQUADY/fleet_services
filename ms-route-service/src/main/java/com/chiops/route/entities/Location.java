package com.chiops.route.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_latitude", precision = 10, scale = 6)
    private BigDecimal startLatitude;

    @Column(name = "start_longitude", precision = 10, scale = 6)
    private BigDecimal startLongitude;

    @Column(name = "end_latitude", precision = 10, scale = 6)
    private BigDecimal endLatitude;

    @Column(name = "end_longitude", precision = 10, scale = 6)
    private BigDecimal endLongitude;

    public Location() {
    }

    public Location(BigDecimal startLatitude, BigDecimal startLongitude, BigDecimal endLatitude, BigDecimal endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public BigDecimal getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(BigDecimal endLatitude) {
        this.endLatitude = endLatitude;
    }

    public BigDecimal getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(BigDecimal endLongitude) {
        this.endLongitude = endLongitude;
    }
}
