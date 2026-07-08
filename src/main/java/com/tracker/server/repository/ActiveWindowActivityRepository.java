//package com.tracker.server.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.tracker.server.entity.ActiveWindowActivity;
//
//@Repository
//public interface ActiveWindowActivityRepository
//        extends JpaRepository<ActiveWindowActivity, Long> {
//	List<ActiveWindowActivity> findByDeviceId(Long deviceId);
//}

package com.tracker.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tracker.server.entity.ActiveWindowActivity;

@Repository
public interface ActiveWindowActivityRepository extends JpaRepository<ActiveWindowActivity, Long>, JpaSpecificationExecutor<ActiveWindowActivity> {
    
    List<ActiveWindowActivity>
    findByDeviceIdOrderByIdDesc(
            Long deviceId);

//    List<ActiveWindowActivity>
//    findByUser_IdOrderByIdDesc(
//            Long userId);
    
    Optional<ActiveWindowActivity> findByOfflineId(String offlineId);
}