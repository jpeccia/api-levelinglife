package com.jpeccia.levelinglife.service;

import com.jpeccia.levelinglife.entity.FriendRequest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.FriendRequestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public String sendFriendRequest(User sender, String receiverUsername) {
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verifica se o pedido de amizade já existe
        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            return "Pedido de amizade já enviado ou já são amigos.";
        }

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequestRepository.save(friendRequest);
        return "Pedido de amizade enviado com sucesso.";
    }

    public String respondToFriendRequest(User receiver, Long requestId, boolean accept) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Pedido de amizade não encontrado"));

        if (!friendRequest.getReceiver().equals(receiver)) {
            return "Você não tem permissão para responder a este pedido.";
        }

        friendRequest.setStatus(accept ? FriendRequest.Status.ACCEPTED : FriendRequest.Status.DECLINED);
        friendRequestRepository.save(friendRequest);

        if (accept) {
            // Cria amizade bidirecional
            friendRequestRepository.save(new FriendRequest(friendRequest.getReceiver(), friendRequest.getSender()));
        }

        return accept ? "Pedido de amizade aceito." : "Pedido de amizade recusado.";
    }

    public List<FriendRequest> getPendingFriendRequests(User receiver) {
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequest.Status.PENDING);
    }
}