package process;

import settings.Settings;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ProcessHandler {
    private final static Logger LOGGER = Logger.getLogger(ProcessHandler.class.getName());

    private Vector<String> singleGPUProcesses;
    private Vector<String> stopMiningProcesses;
    private boolean currentProcessIsStop;
    private String currentVisibleProcess;
    private Settings settings;
    private ProcessListener processListener;

    private ScheduledExecutorService scheduledPool;

    public ProcessHandler(Settings settings, ProcessListener processListener) {

        this.settings = settings;
        this.processListener = processListener;
        singleGPUProcesses = new Vector<>();
        stopMiningProcesses = new Vector<>();
        String stopPrograms = (String) settings.get(Settings.STOP_PROGRAMS);
        if (stopPrograms.length() > 0) {
            stopMiningProcesses.addAll(Arrays.asList(stopPrograms.split(", ")));
        }
        String singlePrograms = (String) settings.get(Settings.SINGLE_PROGRAMS);
        if (singlePrograms.length() > 0) {
            singleGPUProcesses.addAll(Arrays.asList(singlePrograms.split(", ")));
        }

        scheduledPool = Executors.newSingleThreadScheduledExecutor();

        scheduledPool.scheduleWithFixedDelay(() -> {
            Stream<ProcessHandle> processHandleStream = ProcessHandle.allProcesses();
            AtomicBoolean stopStartedProcess = new AtomicBoolean(false);
            AtomicBoolean isReturned = new AtomicBoolean(false);
            processHandleStream.forEach(process -> {
                ProcessHandle.Info info = process.info();
                boolean stop = false, single = false, alreadyStarted = isAlreadyStarted(info);

                if (!isReturned.get()){
                    if (currentVisibleProcess == null || !currentProcessIsStop) {
                        stop = isStopProgram(info);
                        if (!stop && currentVisibleProcess == null)
                            single = isSingleProgram(info);

                        if (stop || single) {
                            if (stop && !currentProcessIsStop && !alreadyStarted) {
                                currentVisibleProcess = info.command().get();
                                currentProcessIsStop = true;
                                isReturned.set(true);
                                processListener.startStopProgram();
                            } else if (single && !alreadyStarted) {
                                currentVisibleProcess = info.command().get();
                                currentProcessIsStop = false;
                                isReturned.set(true);
                                processListener.startSingleProgram();
                            }
                        }
                    }
                    if (!isReturned.get()){
                        if (alreadyStarted) {
                            stopStartedProcess.set(false);
                            isReturned.set(true);
                        } else
                            stopStartedProcess.set(true);
                    }
                }
            });
            if (!isReturned.get() && stopStartedProcess.get() && currentVisibleProcess != null){
                currentVisibleProcess = null;
                if (currentProcessIsStop)
                    processListener.stopStopProgram();
                else
                    processListener.stopSingleProgram();
                currentProcessIsStop = false;
            }
        }, 0,5, TimeUnit.SECONDS);
    }

    private boolean isStopProgram(ProcessHandle.Info info) {
        for (int i = 0; i < stopMiningProcesses.size(); i++) {
            if (info.command().isPresent())
                if (stopMiningProcesses.get(i).equals(info.command().get()))
                    return true;
        }
        return false;
    }

    private boolean isSingleProgram(ProcessHandle.Info info) {
        for (int i = 0; i < singleGPUProcesses.size(); i++) {
            if (info.command().isPresent())
                if (singleGPUProcesses.get(i).equals(info.command().get()))
                    return true;
        }
        return false;
    }

    private boolean isAlreadyStarted(ProcessHandle.Info info) {
        if (currentVisibleProcess != null && info.command().isPresent()) {
            return currentVisibleProcess.equals(info.command().get());
        }
        return false;
    }

    private Vector<String> addProgram(Vector<String> list, String path, String key) {
        if (path != null) {
            list.add(path);
            if (list.size() > 0) {
                String arrSave = list.toString();
                settings.setProperty(key, arrSave.substring(1, arrSave.length() - 1));
                try {
                    settings.saveSettings();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Не удалось записать настройки в файл", e);
                }
            }
        }
        return list;
    }

    public Vector<String> addSingleProgram(String path) {
        return addProgram(singleGPUProcesses, path, Settings.SINGLE_PROGRAMS);
    }

    public Vector<String> addStopProgram(String path) {
        return addProgram(stopMiningProcesses, path, Settings.STOP_PROGRAMS);
    }

    private Vector<String> delProgram(Vector<String> list, String path, String key) {
        if (path != null) {
            list.remove(path);
            String arrSave = list.toString();
            settings.setProperty(key, arrSave.substring(1, arrSave.length() - 1));
            try {
                settings.saveSettings();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Не удалось записать настройки в файл", e);
            }
        }
        return list;
    }

    public Vector<String> getSingleGPUProcesses() {
        return singleGPUProcesses;
    }

    public Vector<String> delSingleProgram(String path) {
        return delProgram(singleGPUProcesses, path, Settings.SINGLE_PROGRAMS);
    }

    public Vector<String> delStopProgram(String path) {
        return delProgram(stopMiningProcesses, path, Settings.STOP_PROGRAMS);
    }

    public Vector<String> getStopMiningProcesses() {
        return stopMiningProcesses;
    }
}
