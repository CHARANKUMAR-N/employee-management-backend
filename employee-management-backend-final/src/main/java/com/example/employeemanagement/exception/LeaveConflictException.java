package com.example.employeemanagement.exception;

public class LeaveConflictException extends RuntimeException {
    public LeaveConflictException(String message) {
        super(message);
    }
}