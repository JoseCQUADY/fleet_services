package com.chiops.route.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_route")
    private Long id;

    @Column(name = "vehicle_id")
    private String vehicleVin;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "travel_date")
    private LocalDateTime travelDate;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_location")
    private Location location;

    @ManyToOne()
    @JoinColumn(name = "id_problem", nullable = true)
    private Problem problem;

    public Route() {
    }

    public Route(String vehicleVin, String routeName, LocalDateTime travelDate, LocalDateTime creationDate, Location location) {
        this.vehicleVin = vehicleVin;
        this.routeName = routeName;
        this.travelDate = travelDate;
        this.creationDate = creationDate;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public LocalDateTime getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDateTime travelDate) {
        this.travelDate = travelDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
