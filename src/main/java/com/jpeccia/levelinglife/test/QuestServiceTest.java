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

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.QuestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.QuestService;

class QuestServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestService questService;

    private User user;
    private QuestDTO questDTO;

    @BeforeEach
    void setUp(){
        // Configuração inicial de dados de teste
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        questDTO = new QuestDTO();
        questDTO.setTitle("Complete a daily task");
        questDTO.setDescription("This is a daily quest");   }

   @Test    
   void testCreateQuest(){
        // Configura o QuestDTO sem definir o tipo de quest
        questDTO.setType(null);

        // Configura o mock do repositório de usuários para retornar o usuário
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Configura o mock do repositório de quests para retornar a quest ao salvar
        when(questRepository.save(any(Quest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Chama o método que está sendo testado
        Quest createdQuest = questService.createQuest(questDTO, user.getId(), questDTO.getDueDate());

        // Verificações
        assertEquals(questDTO.getTitle(), createdQuest.getTitle());
        assertEquals(questDTO.getDescription(), createdQuest.getDescription());
        assertEquals(0, createdQuest.getXp()); // Verifica se o XP foi definido como 0 (valor padrão)
        assertEquals(null, createdQuest.getExpiresAt()); // Verifica se a data de expiração está como null

        // Verifica se o repositório foi chamado corretamente para salvar a quest
        verify(questRepository, times(1)).save(any(Quest.class));
   }
}
