package com.example.todoapp.task;

public class TaskMapper {

    public static Task fromDTO(TaskDTO taskDTO) {
        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDeadline(taskDTO.getDeadline());
        task.setPriority(taskDTO.getPriority());
        task.setCompleted(taskDTO.getComplete());
        task.setUserId(taskDTO.getUserId());
        return task;
    }

    public static TaskDTO toDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName(task.getName());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setComplete(task.getCompleted());
        taskDTO.setUserId(task.getUserId());
        return taskDTO;
    }
}
