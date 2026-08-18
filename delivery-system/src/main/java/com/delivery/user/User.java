package com.delivery.user;

import com.delivery.common.BaseEntity;
import com.delivery.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(name = "first_name", length = 50)
    private String firstName;
    @Column(name = "last_name",length=50)
    private String lastName;
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Wallet wallet;
}
