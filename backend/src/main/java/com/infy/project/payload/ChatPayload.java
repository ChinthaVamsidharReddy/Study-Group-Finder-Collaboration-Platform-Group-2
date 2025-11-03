package com.infy.project.payload;

public record ChatPayload(String type, Long groupId, Object message) {}