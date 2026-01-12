package com.ks.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NodeManagementService {

    final Logger logger = LoggerFactory.getLogger(NodeManagementService.class);

    public String getNodeDetails(String nodeId) {
        logger.info("getNodeDetails called for nodeId: {}", nodeId);
        return String.format("""
                Node Details for %s:
                - Status: Available
                - CPU Usage: 45%%
                - Memory: 2.3GB/8GB
                - Uptime: 23h 45m
                - Last Check: 2026-01-07T20:29:00Z
                """, nodeId);
    }

    public String performBasicCheck(String nodeId, String checkType) {
        logger.info("performBasicCheck called for nodeId: {}", nodeId);
        return String.format("""
                Basic Check Results for %s:
                - Ping: SUCCESS (12ms)
                - SSH Port 22: OPEN
                - Disk Space: 78%% used
                - Overall: HEALTHY
                """, nodeId);
    }

    public String fetchServiceStatus(String nodeId, String serviceName) {
        logger.info("fetchServiceStatus called for nodeId: {} and serviceName: {}", nodeId, serviceName);
        return String.format("Service %s status: STOPPED", serviceName);
    }

    public String restartNodeService(String nodeId, String serviceName) {
        logger.info("restartNodeService called for nodeId: {} and serviceName: {}", nodeId, serviceName);

        return String.format("""
                Service Restart Initiated:
                - Node: %s
                - Service: %s
                - Action: RESTART
                - Status: SUCCESSFULL
                """, nodeId, serviceName);
    }

    public String sendNotification(String nodeId, String message, String priority) {
        logger.info("sendNotification called for nodeId: {} and message: {}", nodeId, message);
        String level = priority != null ? priority : "MEDIUM";
        return String.format("""
                Notification Sent:
                - Node: %s
                - Priority: %s
                - Message: %s
                - Channels: Slack, Email, PagerDuty
                - Status: DELIVERED
                """, nodeId, level, message);
    }
}
