package ca.rdmss.test.dflow.impl;

import java.util.concurrent.atomic.AtomicInteger;

import ca.rdmss.dflow.TaskAsync;
import ca.rdmss.dflow.TaskTransition;

public class TestTaskAsync extends TaskAsync<TestContext> {

	final private String taskId;
	final private boolean isException;
	final private TaskTransition transition;
	private AtomicInteger fail;
	
	public TestTaskAsync(String taskId) {
		this(taskId, true);
	}

	public TestTaskAsync(String taskId, boolean isNotException) {
		this(taskId, isNotException, null);
	}

	public TestTaskAsync(String taskId, TaskTransition transition) {
		this(taskId, transition, null);
	}

	public TestTaskAsync(String taskId, TaskTransition transition, AtomicInteger fail) {
		this.taskId = taskId;
		this.transition = transition;
		this.fail = fail;
		this.isException = false;
	}

	public TestTaskAsync(String taskId, boolean isNotException, AtomicInteger fail) {
		this.taskId = taskId;
		this.isException = !isNotException;
		this.fail = fail;
		this.transition = null;
	}

	@Override
	public TaskTransition execute(TestContext context) throws Throwable {
		
		if( context.isPrint ){
			System.out.println(this);
		}

		context.track(taskId);
		
		if( isException ){
			context.track(taskId);
			if( fail == null ){
				throw new RuntimeException(String.format(" %s get failed by test reason!", this));
			} else if( fail.get() > 0 ){
				fail.decrementAndGet();
				throw new RuntimeException(String.format(" %s get failed by test reason!", this));
			}
		} else if( transition != null ){
			if( fail == null ){
				return transition;
			} else if( fail.get() > 0 ){
				fail.decrementAndGet();
				return transition;
			}
		}
		
		return TaskTransition.Next;
	}

	@Override
	public String toString() {
		return String.format("Test async #%s", taskId);
	}
}
