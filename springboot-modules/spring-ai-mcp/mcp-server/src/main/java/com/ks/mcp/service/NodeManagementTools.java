package com.ks.mcp.service;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeManagementTools {
    @Autowired
    private NodeManagementService nodeManagementService;

    @McpTool(name = "GetNodeDetails", description = """
            Retrieve detailed information about a specific node including status,
            configuration, and metrics
            """)
    public String getNodeDetails(
            @McpToolParam(description = "Node identifier (ID or hostname)", required = true) String nodeId) {
        return nodeManagementService.getNodeDetails(nodeId);
    }

    @McpTool(name = "performBasicCheck", description = "Perform health and connectivity checks on a node without making changes")
    public String performBasicCheck(
            @McpToolParam(description = "Node identifier", required = true) String nodeId,
            @McpToolParam(description = "Optional specific check type (ping, port, service)") String checkType) {
        return nodeManagementService.performBasicCheck(nodeId, checkType);
    }

    @McpTool(name = "fetchServiceStatus", description = "Fetch the status of a specific service on the target node.")
    public String fetchServiceStatus(
            @McpToolParam(description = "Node identifier", required = true) String nodeId,
            @McpToolParam(description = "Service name to fetch status for", required = true) String serviceName) {
        return nodeManagementService.fetchServiceStatus(nodeId, serviceName);
    }

    @McpTool(name = "restartNodeService", description = "Restart a specific service on the target node.")
    public String restartNodeService(
            @McpToolParam(description = "Node identifier", required = true) String nodeId,
            @McpToolParam(description = "Service name to restart", required = true) String serviceName) {
        return nodeManagementService.restartNodeService(nodeId, serviceName);
    }

    @McpTool(name = "sendNotification", description = "Send notification to node operators or monitoring systems")
    public String sendNotification(
            @McpToolParam(description = "Node identifier", required = true) String nodeId,
            @McpToolParam(description = "Notification message", required = true) String message,
            @McpToolParam(description = "Priority level (LOW, MEDIUM, HIGH, CRITICAL)", required = false) String priority) {
        return nodeManagementService.sendNotification(nodeId, message, priority);
    }
}
