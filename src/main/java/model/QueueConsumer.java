package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

class QueueConsumer<T> implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);
    private Consumer<T> toDo;
    private BlockingQueue<T> queue;
    QueueConsumer(Consumer<T> toDo, BlockingQueue<T> queue) {
        this.toDo = toDo;
        this.queue = queue;
    }
    public void close(){
        Thread.currentThread().interrupt();
        log.warn("closing consumer thread");
    }

    @Override
    public void run() {
        try {
            while (true) {
                toDo.accept(queue.take());
            }
        } catch (InterruptedException e){
            log.warn("consumer was interrupted",e);
        }
    }
}
