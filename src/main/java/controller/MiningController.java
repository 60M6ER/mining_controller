package controller;

import gui.GUI;
import process.MinerListener;
import process.ProcessListener;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiningController implements ProcessListener {
    private final static Logger LOGGER = Logger.getLogger(MiningController.class.getName());
    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private static final String COMMAND_MINER = "./t-rex.exe";
    private static final String COMMAND_PILL = "./OhGodAnETHlargementPill-r2.exe";

    private boolean active = false;
    private Date start_date;
    private Date end_date;
    private boolean allTime;
    private String wallet;
    private int indexSingleGPU;
    private ScheduledExecutorService scheduledPool;

    private boolean miningStarted = false;
    private boolean single = false;
    private boolean stop = false;
    private boolean timeTrue = false;

    private Process minerProcess;
    private Process pillProcess;

    private GUI gui;
    private MinerListener minerListener;

    public MiningController(GUI gui) {
        this.gui = gui;
        minerListener = gui;
        active = Boolean.parseBoolean((String) gui.getSettings().get(Settings.ACTIVE));
        allTime = Boolean.parseBoolean((String) gui.getSettings().get(Settings.ALL_TIME));
        try {
            start_date = df.parse((String) gui.getSettings().get(Settings.START_TIME));
            end_date = df.parse((String) gui.getSettings().get(Settings.END_TIME));
            //end_date.setTime(end_date.getTime() + (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }

        wallet = gui.getSettings().getProperty(Settings.WALLET);
        indexSingleGPU = Integer.parseInt(gui.getSettings().getProperty(Settings.INDEX_SINGLE_GPU));

        scheduledPool = Executors.newSingleThreadScheduledExecutor();
        scheduledPool.scheduleWithFixedDelay(() -> {
            if (!allTime){
                Date curDate = new Date();
                try {
                    curDate = df.parse(df.format(curDate));
                    if (curDate.after(start_date) || curDate.before(end_date))
                        timeTrue();
                    else
                        timeFalse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else timeTrue();
        }, 10, 30, TimeUnit.SECONDS);
    }

    private void startMining() {
        if (!timeTrue || stop || miningStarted || !active)
            return;
        List<String> argumentsPill = new ArrayList<>();
        argumentsPill.add(COMMAND_PILL);
        if (single)
            argumentsPill.add("--revA " + indexSingleGPU);
        List<String> argumentsMiner = new ArrayList<>();
        argumentsMiner.add(COMMAND_MINER);
        argumentsMiner.add("-a ethash");
        argumentsMiner.add("-o stratum+tcp://ru-eth.hiveon.net:4444");
        argumentsMiner.add("-u " + wallet);
        argumentsMiner.add("-p x");
        if (single)
            argumentsMiner.add("-d " + indexSingleGPU);
        ProcessBuilder miner = new ProcessBuilder(argumentsMiner);
        ProcessBuilder pill = new ProcessBuilder(argumentsPill);
        //ProcessBuilder miner = new ProcessBuilder("./ETH-hiveos.bat");
        File log = new File("miner.log");
        miner.redirectErrorStream(true);
        miner.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
        try {
            pillProcess = pill.start();
            minerProcess = miner.start();
            miningStarted = true;
            minerListener.miningStarted("Запущен майнинг в " + (single ? "сингл" : "полном") + " режиме", single);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopMining() {
        if (miningStarted) {
            if (minerProcess.isAlive())
                minerProcess.destroy();
            if (pillProcess.isAlive())
                pillProcess.destroy();
            miningStarted = false;
            minerListener.miningStopped("Майнинг остановлен");
        }
    }


    private void timeTrue() {
        LOGGER.info("Время подходит для старта");
        timeTrue = true;
        if (!miningStarted)
            startMining();
    }

    private void timeFalse() {
        LOGGER.info("Время неподходит для старта");
        timeTrue = false;
        if (miningStarted)
            stopMining();
    }

    public void setAllTime(boolean allTime) {
        this.allTime = allTime;
        gui.getSettings().setProperty(Settings.ALL_TIME, String.valueOf(allTime));
        try {
            gui.getSettings().saveSettings();
        } catch (IOException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
        gui.getSettings().setProperty(Settings.WALLET, wallet);
        try {
            gui.getSettings().saveSettings();
        } catch (IOException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }
    }

    public void setIndexSingleGPU(int index) {
        this.indexSingleGPU = index;
        gui.getSettings().setProperty(Settings.INDEX_SINGLE_GPU, Integer.toString(index));
        try {
            gui.getSettings().saveSettings();
        } catch (IOException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        gui.getSettings().setProperty(Settings.ACTIVE, Boolean.toString(active));
        try {
            gui.getSettings().saveSettings();
        } catch (IOException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }

        if (miningStarted && !active) {
            stopMining();
        }
        if (!miningStarted && active) {
            startMining();
        }
    }

    @Override
    public void startSingleProgram() {
        LOGGER.log(Level.INFO, "Запущен сингл процесс");
        single = true;
        if (miningStarted)
            stopMining();
        startMining();
    }

    @Override
    public void stopSingleProgram() {
        LOGGER.log(Level.INFO, "Остановлен сингл процесс");
        single = false;
        if (miningStarted)
            stopMining();
        startMining();
    }

    @Override
    public void startStopProgram() {
        LOGGER.log(Level.INFO, "Запущен стоп процесс");
        stop = true;
        if (miningStarted)
            stopMining();
    }

    @Override
    public void stopStopProgram() {
        LOGGER.log(Level.INFO, "Остановлен стоп процесс");
        stop = false;
        if (!miningStarted)
            startMining();
    }
}
