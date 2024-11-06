package com.jpeccia.levelinglife.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.entity.QuestType;
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
    private QuestDTO quest;

    void setUp(){
        //Inicializa os objetos de teste
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setXp(200);
        user.setLevel(1);

        quest = new QuestDTO();
        quest.setTitle("Complete a task");
        quest.setDescription("Description of the task");
        quest.setType(DAILY);
   }

   @Test    
   void testCreateQuest(){
        when(questRepository.save(any(QuestD.class))).thenReturn(quest);

        Quest createdQuest = questService.createQuest(quest, user.getId());
   }
}
