package model;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

class QueueConsumer<T> implements Runnable{
    private Consumer<T> toDo;
    private BlockingQueue<T> queue;
    QueueConsumer(Consumer<T> toDo, BlockingQueue<T> queue) {
        this.toDo = toDo;
        this.queue = queue;
    }
    public void close(){
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        try {
            while (true) {
                toDo.accept(queue.take());
            }
        } catch (InterruptedException e){
            //e.printStackTrace(); donothing - Exception is normal here
        }
    }
}
