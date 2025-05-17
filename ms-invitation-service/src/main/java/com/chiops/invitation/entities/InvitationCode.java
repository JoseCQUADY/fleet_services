package com.chiops.invitation.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Table(name = "invitation_code")
public class InvitationCode {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id")
    private Long id;
    
    @NotNull
    @Column(name = "code" , unique = true, nullable = false)
    private String code;

    @Column(name = "is_used")
    private String status;

    public InvitationCode(String status, String code) {
        this.code = code;
        this.status = status;
    }

    public InvitationCode() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}