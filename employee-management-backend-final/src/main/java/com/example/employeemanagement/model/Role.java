package com.example.employeemanagement.model;

public enum Role {
    ADMIN("admin"),
    SENIOR_PROJECT_MANAGER("senior_project_manager"),
    PROJECT_MANAGER("project_manager"),
    TEAM_MANAGER("team_manager"),
    MEMBER("member");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
