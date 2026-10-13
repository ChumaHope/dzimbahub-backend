package zw.ac.msu.dzimbahub.auth;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="app_users", uniqueConstraints={@UniqueConstraint(name="uk_app_users_email", columnNames="email")})
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, length=100) private String fullName;
 @Column(nullable=false, unique=true, length=180) private String email;
 @Column(nullable=false, length=100) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private UserRole role;
 @Column(nullable=false) private Instant createdAt=Instant.now();
 protected AppUser() {}
 public AppUser(String fullName,String email,String passwordHash,UserRole role){this.fullName=fullName;this.email=email;this.passwordHash=passwordHash;this.role=role;this.createdAt=Instant.now();}
 public Long getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public UserRole getRole(){return role;} public Instant getCreatedAt(){return createdAt;}
}
