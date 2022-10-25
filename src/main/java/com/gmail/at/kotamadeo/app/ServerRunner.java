package com.gmail.at.kotamadeo.app;

import com.gmail.at.kotamadeo.server.ServerGuiController;

public class ServerRunner {
    public static void main(String[] args) {
        ServerGuiController serverGuiController = new ServerGuiController();
        serverGuiController.run(serverGuiController);
    }
}
