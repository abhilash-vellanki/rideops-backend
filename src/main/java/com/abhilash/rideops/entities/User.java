package com.abhilash.rideops.entities;

import com.abhilash.rideops.entities.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * name: the generator name used by Java/JPA.
 * sequenceName: the actual sequence name in PostgreSQL.
 * allocationSize: how many IDs Hibernate reserves at once.
 * generator: connects @GeneratedValue to the named generator.
 * */
@Entity
@Getter
@Setter
@Table(name = "app_user",
        indexes = {
                @Index(name="idx_user_email",columnList = "email")
        })
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "app_user_id_generator", sequenceName = "app_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_generator")
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
