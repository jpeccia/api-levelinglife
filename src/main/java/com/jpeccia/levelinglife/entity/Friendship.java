package com.jpeccia.levelinglife.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    public Friendship() {}

    public Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
}