package org.kasung;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class FinancialReportProcess {

	public static void main(String[] args) {
		//Create Activiti process engine
		 ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.
				createStandaloneProcessEngineConfiguration();
		 processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		 processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti");
		 processEngineConfiguration.setJdbcUsername("root");
		 processEngineConfiguration.setJdbcPassword("root");
		 
		 processEngineConfiguration.setDatabaseSchemaUpdate("true");
		 ProcessEngine processEngine =	processEngineConfiguration.buildProcessEngine();
		
		//Get Activiti services
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		//Deploy the process definition
		repositoryService.createDeployment()
		.addClasspathResource("FinancialReportProcess.bpmn20.xml")
		.deploy();
		
		startProcessInstance(runtimeService);
		startProcessInstance(runtimeService);

		
		TaskService taskService = processEngine.getTaskService();

		completeTask(taskService, "fozzie"); //accountant
		completeTask(taskService, "kermit"); //management
		
		System.out.println("Completed.");
		
	}
	
	private static String startProcessInstance(RuntimeService runtimeService) {
		
		//Start a process instance
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("financialReport");
		 
		System.out.println("Instance Created. Id - " + processInstance.getId());
		return processInstance.getId();	
	}
	
	private static void completeTask(TaskService taskService, String user) {
		System.out.println("============ User " + user + "============");
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
//		
//		for (Task task : tasks) {
//			String assignee = task.getId();
//			System.out.println("Assignee by Group - " + assignee);
//		}
		
		tasks = taskService.createTaskQuery().taskCandidateUser(user).list();
		for (Task task : tasks) {
			String assignee = task.getId();
			System.out.println("Task Candidate - " + assignee + ". Name - " + task.getTaskDefinitionKey());
			
			taskService.claim(task.getId(), user);
			System.out.println("Claimed the task. - " + task.getId() );
		}
		
		System.out.println("---------------");
		
		tasks = taskService.createTaskQuery().taskAssignee(user).list();
		for (Task task : tasks) {
			String assignee = task.getAssignee();
			System.out.println("Task - " + task.getId() +" Assignee - " + assignee 
					+ ". Name - " + task.getTaskDefinitionKey());
			
			taskService.complete(task.getId());
			System.out.println("Completed the task - " + task.getId());
		}
	}

}
