package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_actions")
public class ReportActions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "action_id", columnDefinition = "BINARY(16)")
    private UUID actionId;

    @ManyToOne
    @JoinColumn(name = "reportid", nullable = false)
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ReportActions() {}

    public ReportActions(Report report, ActionType actionType, String note) {
        this.report = report;
        this.actionType = actionType;
        this.note = note;
    }

    public UUID getActionId() {
        return actionId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
