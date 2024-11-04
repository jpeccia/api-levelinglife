package com.jpeccia.levelinglife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.entity.Friendship;
import com.jpeccia.levelinglife.service.FriendshipService;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/request")
    public Friendship sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long friendId) {
        return friendshipService.sendFriendRequest(requesterId, friendId);
    }

    @PostMapping("/accept")
    public Friendship acceptFriendRequest(@RequestParam Long friendshipId) {
        return friendshipService.acceptFriendRequest(friendshipId);
    }

    @PostMapping("/reject")
    public Friendship rejectFriendRequest(@RequestParam Long friendshipId) {
        return friendshipService.rejectFriendRequest(friendshipId);
    }

    @GetMapping("/{userId}/list")
    public List<Friendship> listFriends(@PathVariable Long userId) {
        return friendshipService.listFriends(userId);
    }
}