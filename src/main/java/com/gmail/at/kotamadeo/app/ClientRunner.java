package com.gmail.at.kotamadeo.app;

import com.gmail.at.kotamadeo.client.ClientGuiController;

public class ClientRunner {
    public static void main(String[] args) {
        ClientGuiController clientGuiController = new ClientGuiController();
        clientGuiController.run(clientGuiController);
    }
}
