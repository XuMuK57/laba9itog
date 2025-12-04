package com.example.taskplanner;

public final class Api {

    public static final String BASE_URL = "http://10.0.2.2/backend/";

    public static final String LOGIN = BASE_URL + "login.php";
    public static final String SIGNUP = BASE_URL + "signup.php";

    public static final String LIST_TASKS = BASE_URL + "list_tasks.php";
    public static final String CREATE_TASK = BASE_URL + "create_task.php";
    public static final String UPDATE_TASK = BASE_URL + "update_task.php";
    public static final String DELETE_TASK = BASE_URL + "delete_task.php";

    private Api() {}
}
