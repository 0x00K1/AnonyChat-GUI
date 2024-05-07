package com.anonychat.main.GUI;

public interface AnonyFace {
    long SERIAL_VERSION_UID = 1L;
	void initializeComponents();
	void setupActions();
    boolean validateInput();
}