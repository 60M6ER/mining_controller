package gui;

import javax.swing.*;

public interface AddDelProgramListener {
    void addProgram(String path, JList<String> list);
    void delProgram(JList<String> list);
}
