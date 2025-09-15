package com.example.AdminService.dto.response;

import java.util.List;
import java.util.UUID;

public record AssignInternsResponse (Long taskId,
                                     String taskTitle,
                                     List<UUID> interns){
}
