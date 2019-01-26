package sec09_exam02_blocking;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompletionServiceExample {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());

		CompletionService<Integer> completionService =
				new ExecutorCompletionService<Integer>(executorService);
// CompletionService로 먼저 작업이 처리된 작업을 가져와서 진행해도 상관없을때 효율적으로 이용할 수 있다.
		System.out.println("작업 처리 요청");
		for(int i=1;i<=3;i++) {
			completionService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int sum =0;
					for(int i=1;i<=10;i++)
						sum += i;
					return sum;
				}
			});
		}

		System.out.println("처리 완료된 작업 확인");
		executorService.submit(new Runnable() {
			// UI Thread 에서 take()메소드를 사용하면 UI 작업이 안되기 때문에 take는
			// 항상 다른 Thread에서 사용해야한다
			@Override
			public void run() {
				while(true) {
					try {
						Future<Integer> future = completionService.take(); 
						int value = future.get();
						System.out.println("처리 결과: "+value);
					} catch (Exception e) {
						break;
					}
				}
			}
		});
		executorService.shutdownNow();
	}
}
