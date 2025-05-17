package com.chiops.route.libs.dtos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Introspected
@Serdeable
public class RouteDTO {
    @NotBlank
    private String routeName;
    @NotBlank
    private String vehicleVin;
    @NotNull
    private LocalDateTime travelDate;
    @NotNull
    private LocalDateTime creationDate;
    @NotNull
    private BigDecimal startLatitude;
    @NotNull
    private BigDecimal startLongitude;
    @NotNull
    private BigDecimal endLatitude;
    @NotNull
    private BigDecimal endLongitude;
    private String name;
    private String description;
    private String comment;

    public RouteDTO() {
    }
    public RouteDTO(String routeName, String vehicleVin, LocalDateTime travelDate, LocalDateTime creationDate,
                    BigDecimal startLatitude, BigDecimal startLongitude, BigDecimal endLatitude,
                    BigDecimal endLongitude, String name, String description, String comment) {
        this.routeName = routeName;
        this.vehicleVin = vehicleVin;
        this.travelDate = travelDate;
        this.creationDate = creationDate;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.name = name;
        this.description = description;
        this.comment = comment;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(BigDecimal endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(BigDecimal endLatitude) {
        this.endLatitude = endLatitude;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDateTime travelDate) {
        this.travelDate = travelDate;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }
}
