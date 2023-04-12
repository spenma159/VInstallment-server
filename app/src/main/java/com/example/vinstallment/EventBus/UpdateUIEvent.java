package com.example.vinstallment.EventBus;

public class UpdateUIEvent {
    public int index;
    public boolean enabled;

    public UpdateUIEvent(int index, boolean enabled) {
        this.index = index;
        this.enabled = enabled;
    }
}
