package com.tracker.server.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tracker.server.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceStatusScheduler {

    private final DeviceRepository repository;

    @Scheduled(fixedDelay = 60000)
    public void checkOffline() {

        LocalDateTime limit =
                LocalDateTime.now()
                        .minusMinutes(2);

        repository.findAll()
                .forEach(device -> {

                    if (device.getLastSeen() == null) {
                        return;
                    }

                    if (device.getLastSeen().isBefore(limit)) {

                        device.setOnline(false);

                        repository.save(device);
                    }

                });

    }
}