package com.example.Oboe.DTOs;

import com.example.Oboe.DTOs.ActivityDTO;
import org.springframework.data.domain.Page;

public class StatisticalUserDetailDTO {
    private Page<ActivityDTO> activities;

    public StatisticalUserDetailDTO(Page<ActivityDTO> activities) {
        this.activities = activities;
    }

    public Page<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(Page<ActivityDTO> activities) {
        this.activities = activities;
    }
}
