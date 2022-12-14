package com.gmail.at.kotamadeo.client;

import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@NoArgsConstructor
public class ClientGuiModel {

    private Set<String> allUserNicknames = new HashSet<>();

    protected Set<String> getAllNickname() {
        return allUserNicknames;
    }

    protected void addUser(String nickname) {
        allUserNicknames.add(nickname);
    }

    protected void deleteUser(String nickname) {
        allUserNicknames.remove(nickname);
    }

    protected void setUsers(Set<String> users) {
        this.allUserNicknames = users;
    }
}
