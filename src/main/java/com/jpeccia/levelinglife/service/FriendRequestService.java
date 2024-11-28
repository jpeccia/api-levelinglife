package com.jpeccia.levelinglife.service;

import com.jpeccia.levelinglife.entity.FriendRequest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.FriendRequestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

            // Verifica se o usuário está tentando enviar um pedido de amizade para si mesmo
        if (sender.getUsername().equals(receiverUsername)) {
            return "Você não pode enviar um pedido de amizade para si mesmo.";
        }

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

    // Obtém o usuário autenticado
    User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Verifica se o usuário autenticado é o destinatário do pedido comparando os usernames
    if (!friendRequest.getReceiver().getUsername().equals(authenticatedUser.getUsername())) {
        return "Você não tem permissão para responder a este pedido.";
    }

    // Atualiza o status do pedido de amizade
    friendRequest.setStatus(accept ? FriendRequest.Status.ACCEPTED : FriendRequest.Status.DECLINED);
    friendRequestRepository.save(friendRequest);

    // Caso o pedido seja aceito, adiciona amizade bidirecional
    if (accept) {
        FriendRequest reciprocalRequest = new FriendRequest(friendRequest.getReceiver(), friendRequest.getSender());
        reciprocalRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(reciprocalRequest);
    }

    return accept ? "Pedido de amizade aceito." : "Pedido de amizade recusado.";
    }

    @Transactional
    public String removeFriend(User user, String friendUsername) {
    // Encontra o amigo pelo nome de usuário
    User friend = userRepository.findByUsername(friendUsername)
            .orElseThrow(() -> new EntityNotFoundException("Amigo não encontrado"));

    // Verifica se a amizade existe de ambos os lados
    FriendRequest friendship1 = friendRequestRepository.findBySenderAndReceiver(user, friend)
            .orElseThrow(() -> new EntityNotFoundException("Amizade não encontrada entre " + user.getUsername() + " e " + friendUsername));

    FriendRequest friendship2 = friendRequestRepository.findBySenderAndReceiver(friend, user)
            .orElseThrow(() -> new EntityNotFoundException("Amizade não encontrada entre " + friendUsername + " e " + user.getUsername()));

    // Remove ambas as amizades do banco de dados
    friendRequestRepository.delete(friendship1);
    friendRequestRepository.delete(friendship2);

    return "Amizade removida com sucesso entre " + user.getUsername() + " e " + friendUsername;
}

    public List<FriendRequest> getPendingFriendRequests(User receiver) {
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequest.Status.PENDING);
    }

    public List<FriendRequest> getAcceptedFriendRequests(User user) {
        // Busca as solicitações de amizade onde o status é ACCEPTED
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.Status.ACCEPTED);
    }
}