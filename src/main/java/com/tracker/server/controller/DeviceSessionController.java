package com.tracker.server.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.server.entity.Device;
import com.tracker.server.entity.DeviceSession;
import com.tracker.server.repository.DeviceRepository;
import com.tracker.server.repository.DeviceSessionRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class DeviceSessionController {

    private final DeviceSessionRepository repository;
    private final DeviceRepository deviceRepository;

    @PostMapping("/start/{deviceId}")
    public void start(
            @PathVariable Long deviceId) {

        Device device =
                deviceRepository
                        .findById(deviceId)
                        .orElseThrow();

        DeviceSession session =
                DeviceSession.builder()
                        .device(device).user(device.getUser())
                        
                        .startupTime(
                                LocalDateTime.now())
                        .status("RUNNING")
                        .build();

        repository.save(session);
    }

    @PostMapping("/end/{deviceId}")
    public void end(
            @PathVariable Long deviceId) {

        DeviceSession session =
                repository
                        .findTopByDeviceIdAndStatusOrderByIdDesc(
                                deviceId,
                                "RUNNING")
                        .orElseThrow();

        LocalDateTime end =
                LocalDateTime.now();

        session.setShutdownTime(end);

        session.setSessionDurationSeconds(
                Duration.between(
                        session.getStartupTime(),
                        end)
                        .getSeconds());

        session.setStatus("CLOSED");

        repository.save(session);
    }
}