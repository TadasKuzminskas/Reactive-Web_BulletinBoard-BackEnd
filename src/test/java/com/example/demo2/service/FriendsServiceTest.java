package com.example.demo2.service;

import com.example.demo2.model.Friends;
import com.example.demo2.repository.Custom.FriendRepositoryCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class FriendsServiceTest {

    @MockBean
    FriendRepositoryCustom repository;

    @InjectMocks
    FriendsService service;

    private Friends friends;

    private Friends createFriends(Long id, String friend, String username) {
       return Friends.builder()
                .id(id)
                .friend(friend)
                .username(username)
                .build();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        friends = createFriends(1L, "anyFriend", "anyUsername");
    }

    @Test
    public void testGetFriendsByUsername() {
        Mockito.when(repository.getFriendsByUsername(anyString())).thenReturn(Flux.just(friends));
        Flux<Friends> returnedFriends = service.getFriendsByUsername(anyString());
        StepVerifier.create(returnedFriends).expectNext(friends).expectComplete();
    }

    @Test
    public void testAddFriend() {
        Mockito.when(repository.saveFriend(any())).thenReturn(Mono.just(1L));
        Mono<Long> isFriendsAdded = service.addFriend(any());
        StepVerifier.create(isFriendsAdded).expectNext(1L).expectComplete();
    }

    @Test
    public void testDeleteFriend() {
        Mockito.when(repository.deleteFriendById(anyLong())).thenReturn(Mono.just(1));
        Mono<Integer> isFriendsDeleted = service.deleteFriend(any());
        StepVerifier.create(isFriendsDeleted).expectNext(1).expectComplete();
    }

}