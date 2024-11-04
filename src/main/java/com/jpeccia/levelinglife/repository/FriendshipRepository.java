package com.jpeccia.levelinglife.repository;

import com.jpeccia.levelinglife.entity.Friendship;
import com.jpeccia.levelinglife.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findAllByRequesterIdAndStatusOrFriendIdAndStatus(Long requesterId, FriendshipStatus status, Long friendId, FriendshipStatus friendStatus);

}
