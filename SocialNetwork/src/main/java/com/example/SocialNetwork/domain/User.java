package com.example.SocialNetwork.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Where(clause = "deleted = false")
@Getter @Setter
@NoArgsConstructor
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> friends = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> friendOf = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", insertable = false, updatable = false)
    private UserType type;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    public User(String email, String password, String name, String surname, String username) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.type = UserType.User;
    }

    public void addFriend(User friend) {
        friends.add(friend);
        friend.friendOf.add(this);
    }

    public void removeFriend(User friend) {
        friends.remove(friend);
        friend.friendOf.remove(this);
    }
}
