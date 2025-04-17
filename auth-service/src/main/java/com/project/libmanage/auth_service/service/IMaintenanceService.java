package com.project.libmanage.auth_service.service;

public interface IMaintenanceService {

    boolean isMaintenanceMode();

    void setMaintenanceMode(boolean maintenanceMode);
}
