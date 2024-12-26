package tms.tc.model;

public sealed interface WebSocketMessage permits Device, Position, Event {}
