@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Test Task", LocalDate.now().plusDays(1), 3, false, 1L);
        taskDTO = TaskMapper.toDTO(task);
    }

    @Test
    void createTask_returns201() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated());

        Mockito.verify(taskService).createTask(any(Task.class));
    }

    @Test
    void getTaskById_returnsTaskDTO() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.priority").value(3));
    }

    @Test
    void getTasksForUser_returnsList() throws Exception {
        Mockito.when(taskService.getTasksForUser(1L)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Task"));
    }

    @Test
    void updateTask_returns204() throws Exception {
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).updateTask(any(Task.class));
    }

    @Test
    void markTaskAsCompleted_returns200() throws Exception {
        mockMvc.perform(patch("/api/tasks/1/complete"))
                .andExpect(status().isOk());

        Mockito.verify(taskService).markTaskAsCompleted(1L);
    }

    @Test
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).deleteTask(1L);
    }
}