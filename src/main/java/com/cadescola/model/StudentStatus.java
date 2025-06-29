package com.cadescola.model;

public enum StudentStatus {
    ACTIVE("Ativo"),
    INACTIVE("Inativo");
    
    private final String displayName;
    
    StudentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
