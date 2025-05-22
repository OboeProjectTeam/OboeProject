package com.example.Oboe.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;



//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userid", updatable = false, nullable = false)
    private UUID user_id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự!")
    @Column(nullable = false)
    private String passWord;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate day_of_birth;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_USER;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime create_at = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime update_at = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.update_at = LocalDateTime.now();
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public @Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự!") String getPassWord() {
        return passWord;
    }

    public void setPassWord(@Size(min = 8, message = "Mật khẩu ít nhất 8 ký tự!") String passWord) {
        this.passWord = passWord;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDay_of_birth() {
        return day_of_birth;
    }

    public void setDay_of_birth(LocalDate day_of_birth) {
        this.day_of_birth = day_of_birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }
}
