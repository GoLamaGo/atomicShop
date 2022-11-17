import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public class Main {
    static final LongAdder overallSummary = new LongAdder();

    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 3;
        int arrayCapacity = 10;
        Supplier<int[]> genIntsArray = () -> {
            int[] array = new int[arrayCapacity];
            for (int i = 0; i < arrayCapacity; i++) {
                array[i] = new Random().nextInt(1000);
            }
            return array;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                System.out.println("Start of thread" + Thread.currentThread().getName());

                var intsArray = genIntsArray.get();
                System.out.println("Array of numbers for Thread " + Thread.currentThread().getName() + " is " + Arrays.toString(intsArray));

                int summary = 0;

                for (int j = 0; j < intsArray.length; j++) {
                    summary += intsArray[j];
                }

                System.out.println("Intermediate amount from Thread: " + Thread.currentThread().getName() + " is : " + summary);

                overallSummary.add(summary);
                Thread.currentThread().interrupt();
            });
        }

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();

        overallSummary.sum();
        System.out.println("\nOverall summary is " + overallSummary);

    }
}
