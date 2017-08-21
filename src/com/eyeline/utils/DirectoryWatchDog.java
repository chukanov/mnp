package com.eyeline.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Chukanov
 */
public abstract class DirectoryWatchDog {
    private static final Logger log = Logger.getLogger(DirectoryWatchDog.class);

    protected ExecutorService service =
            Executors.newSingleThreadExecutor(new NamedThreadFactory(this.getClass().getSimpleName()+"-WatchDog"));
    protected WatchService watchService;

    protected abstract void processChangedFile(Path filePath) throws Exception;
    protected abstract Path getWatchDir();

    private Runnable listener = new BlankRunnable();

    private boolean shutup = false;
    private Future scheduledFuture;

    public synchronized void start() throws IOException {
        this.shutup = false;
        watchService = FileSystems.getDefault().newWatchService();
        final Path watchDir = getWatchDir();
        watchDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        scheduledFuture = service.submit(() -> {
            while (true) {
                if (shutup) {
                    return;
                }
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException x) {
                    return;
                } catch (ClosedWatchServiceException e){
                    log.warn("",e);
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filePath = ev.context();
                    try {
                        log.debug("changed file: "+filePath);
                        this.processChangedFile(watchDir.resolve(filePath));
                    } catch (Exception e) {
                        log.warn("Can't process file: "+filePath, e);
                    } finally {
                        listener.run();
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        });
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public synchronized void stop() {
        this.shutup = true;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (Exception e) {
                log.warn("Can't close watchService", e);
            }
        }
    }
}
