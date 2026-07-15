package com.tracker.server.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tracker.server.repository.DeviceRepository;
import com.tracker.server.service.RecoveryService;
import com.tracker.server.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceStatusScheduler {

    private final DeviceRepository repository;
    private final RecoveryService recoveryService;

    @Scheduled(fixedDelay = 60000)
    public void checkOffline() {

        LocalDateTime limit =
                DateTimeUtil.now()
                        .minusMinutes(1);

        repository.findAll()
                .forEach(device -> {

                    if (device.getLastSeen() == null) {
                        return;
                    }

                    if (device.getLastSeen().isBefore(limit)) {

                        device.setOnline(false);

                        repository.save(device);
                        recoveryService.recoveryProcess(device.getId());
                        recoveryService.recoveryWindow(device.getId());
                        recoveryService.recoveryIdle(device.getId());
                        recoveryService.recoverySession(device.getId());
                    }

                });

    }
}