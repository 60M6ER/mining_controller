package process;

public interface MinerListener {
    void miningStarted(String mes, boolean single);

    void miningStopped(String mes);
}
