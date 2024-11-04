package com.jpeccia.levelinglife.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Friendship {

    public enum FriendshipStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public Friendship() {
        this.status = FriendshipStatus.PENDING; // O status padrão é "PENDING" ao criar a amizade
    }

    public Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
        this.status = FriendshipStatus.PENDING;
    }
}