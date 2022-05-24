package com.example.SocialNetwork.domain;

import com.example.SocialNetwork.domain.enums.PostStatus;
import com.example.SocialNetwork.domain.enums.UserStatus;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@Where(clause = "deleted = false")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> friends = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> friendOf = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns =  @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password, String name, String surname, String username) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.status = UserStatus.Activated;
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public void removeFriend(User friend) {
        friends.remove(friend);
    }

}
