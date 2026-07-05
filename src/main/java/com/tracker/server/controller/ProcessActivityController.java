package com.tracker.server.controller;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tracker.server.dto.AppUsageDto;
import com.tracker.server.entity.Device;
import com.tracker.server.entity.DeviceSession;
import com.tracker.server.entity.ProcessActivity;
import com.tracker.server.entity.User;
import com.tracker.server.repository.DeviceRepository;
import com.tracker.server.repository.DeviceSessionRepository;
import com.tracker.server.repository.IdleActivityRepository;
import com.tracker.server.repository.ProcessActivityRepository;
import com.tracker.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessActivityController {

    private final ProcessActivityRepository processRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceSessionRepository sessionRepository;
    private final IdleActivityRepository idleRepository;
    
    
    @PostMapping("/recover/{deviceId}")
    public void recoverRunningProcesses(
            @PathVariable Long deviceId) {

    	   List<ProcessActivity> running =
    			   processRepository.findByDeviceIdAndStatus(
    	                    deviceId,
    	                    "RUNNING");

    	    LocalDateTime now = LocalDateTime.now();

    	    for (ProcessActivity p : running) {

    	        p.setEndTime(now);

    	        p.setDurationSeconds(
    	                Duration.between(
    	                        p.getStartTime(),
    	                        now)
    	                        .getSeconds());

    	        p.setStatus("CRASHED");
    	    }

    	    processRepository.saveAll(running);
    }

    @PostMapping("/{deviceId}")
    public ProcessActivity save(
            @PathVariable Long deviceId,
            @RequestBody ProcessActivity activity) {
    	 System.out.println(
    		        "PROCESS RECEIVED : "
    		        + activity.getProcessName());
        Device device =
                deviceRepository.findById(deviceId)
                        .orElseThrow();
        

        activity.setDevice(device);
        activity.setUser(device.getUser());

        return processRepository.save(activity);
    }
    

    
    
    @PutMapping("/{id}")
    public ProcessActivity updateProcess(
            @PathVariable Long id,
            @RequestBody ProcessActivity request) {

        return processRepository.findById(id)
                .map(activity -> {
                    activity.setEndTime(request.getEndTime());
                    activity.setDurationSeconds(request.getDurationSeconds());
                    activity.setStatus(request.getStatus());
                    return processRepository.save(activity);
                })
                .orElse(null);
    }
    
    @GetMapping("/export/processes/{deviceId}")
    public List<ProcessActivity> exportProcesses(
            @PathVariable Long deviceId) {

        return processRepository.findByDeviceId(
                deviceId);
    }
    
    @GetMapping("/export/processes/pdf/{deviceId}")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Long deviceId)
            throws Exception {

        List<ProcessActivity> processes =
                processRepository.findByDeviceId(deviceId);

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        Document document =
                new Document();

        PdfWriter.getInstance(
                document,
                out);

        document.open();

        document.add(
                new Paragraph(
                        "Process Activity Report"));

        document.add(
                new Paragraph(" "));

        PdfPTable table =
                new PdfPTable(5);

        table.addCell("PID");
        table.addCell("Process");
        table.addCell("Status");
        table.addCell("Start");
        table.addCell("End");

        for (ProcessActivity p : processes) {

            table.addCell(
                    String.valueOf(
                            p.getPid()));

            table.addCell(
                    p.getProcessName());

            table.addCell(
                    p.getStatus());

            table.addCell(
                    String.valueOf(
                            p.getStartTime()));

            table.addCell(
                    String.valueOf(
                            p.getEndTime()));
        }

        document.add(table);

        document.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=process-report.pdf")
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(
                        out.toByteArray());
    }
    
    @GetMapping("/my-processes")
    public List<ProcessActivity> myProcesses(
            Authentication auth) {

        String username =
                auth.getName();

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();

        return processRepository
                .findByUser_IdOrderByIdDesc(
                        user.getId());
    }
    
    @GetMapping("/my-sessions")
    public List<DeviceSession> mySessions(
            Authentication auth) {

        String username =
                auth.getName();

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();

        return sessionRepository
                .findByUserIdOrderByIdDesc(
                        user.getId());
    }
    
    @GetMapping("/my-productivity")
    public Map<String,Object> productivity(
            Authentication auth) {

        String username =
                auth.getName();

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();

        Long activeTime =
                processRepository
                        .getTotalActiveTime(
                                user.getId());

        Long idleTime =
                idleRepository
                        .getTotalIdleTime(
                                user.getId());

        List<AppUsageDto> apps =
                processRepository
                        .topApps(
                                user.getId());

        Map<String,Object> map =
                new HashMap<>();

        map.put(
                "activeTime",
                activeTime);

        map.put(
                "idleTime",
                idleTime);

        map.put(
                "apps",
                apps);

        return map;
    }
}