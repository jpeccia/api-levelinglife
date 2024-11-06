package com.jpeccia.levelinglife.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jpeccia.levelinglife.entity.FriendRequest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.FriendRequestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.FriendRequestService;

public class FriendRequestServiceTest {
    
    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendRequestService friendRequestService;

    private User sender;

    private User receiver;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        sender = new User();
        sender.setUsername("sender");

        receiver = new User();
        receiver.setUsername("receiver");
    }

    @Test
    public void testSendFriendRequest(){
        when(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver));

        //Aqui voce chama o metodo do envio do pedido de amizade
        String result = friendRequestService.sendFriendRequest(sender, "receiver");

        assertEquals("Pedido de amizade enviado com sucesso.", result);

        // Verifica se o método save foi chamado para salvar o pedido de amizade
        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    public void testSendFriendRequestWhenReceiverNotFound() {
        when(userRepository.findByUsername("receiver")).thenReturn(Optional.empty());

        // Chama o metodo de envio de pedido de amizade
        String result = friendRequestService.sendFriendRequest(sender, "receiver");

        assertEquals("Usuário não encontrado.", result);
    }

    @Test
    public void testRespondToFriendRequest_Accept() {
        // Criando um pedido de amizade simulado
        FriendRequest request = new FriendRequest(sender, receiver);
        request.setStatus(FriendRequest.Status.PENDING);

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        // Chamando o método de resposta
        String result = friendRequestService.respondToFriendRequest(receiver, 1L, true);

        assertEquals("Pedido de amizade aceito.", result);
        assertEquals(FriendRequest.Status.ACCEPTED, request.getStatus());
        verify(friendRequestRepository, times(1)).save(request);
    }

    @Test
    public void testRespondToFriendRequest_Decline() {
        // Criando um pedido de amizade simulado
        FriendRequest request = new FriendRequest(sender, receiver);
        request.setStatus(FriendRequest.Status.PENDING);

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        // Chamando o método de resposta
        String result = friendRequestService.respondToFriendRequest(receiver, 1L, false);

        assertEquals("Pedido de amizade recusado.", result);
        assertEquals(FriendRequest.Status.DECLINED, request.getStatus());
        verify(friendRequestRepository, times(1)).save(request);
    }

    @Test
    public void testRemoveFriend() {
        // Criando a amizade entre os dois usuários
        FriendRequest acceptedRequest = new FriendRequest(sender, receiver);
        acceptedRequest.setStatus(FriendRequest.Status.ACCEPTED);
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(acceptedRequest));

        // Chamando o método de remoção de amizade
        String result = friendRequestService.removeFriend(sender, "receiver");

        assertEquals("Amizade removida com sucesso.", result);
        verify(friendRequestRepository, times(1)).delete(acceptedRequest);
    }

    @Test
    public void testRemoveFriend_WhenNoFriendshipExists() {
        // Nenhuma amizade existente entre sender e receiver
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.empty());

        // Chamando o método de remoção de amizade
        String result = friendRequestService.removeFriend(sender, "receiver");

        assertEquals("Não há amizade para remover.", result);
    }

}
