package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ContextMenuProcesses extends JPopupMenu implements ActionListener {

    private JMenuItem addProgram;
    private JMenuItem delProgram;
    private JList<String> currentList;
    private GUI gui;
    AddDelProgramListener addDelProgramListener;

    public ContextMenuProcesses(GUI gui, JList<String> currentList) {
        this.gui = gui;
        this.addDelProgramListener = gui;
        this.currentList = currentList;
        addProgram = new JMenuItem("Add");
        addProgram.addActionListener(this);
        delProgram = new JMenuItem("Del");
        delProgram.addActionListener(this);
        this.add(addProgram);
        this.add(delProgram);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addProgram)
            chooseFile();
        if (src == delProgram)
            addDelProgramListener.delProgram(currentList);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileEXEFilter("exe", "EXE"));
        int ret = fileChooser.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            addDelProgramListener.addProgram(file.getAbsolutePath(), currentList);
        }
    }

    private class FileEXEFilter extends FileFilter{
        String extension  ;  // расширение файла
        String description;  // описание типа файлов

        FileEXEFilter(String extension, String description)
        {
            this.extension = extension;
            this.description = description;
        }
        @Override
        public boolean accept(java.io.File file)
        {
            if(file != null) {
                if (file.isDirectory())
                    return true;
                if( extension == null )
                    return (extension.length() == 0);
                return file.getName().toUpperCase().endsWith(extension.toUpperCase());
            }
            return false;
        }
        // Функция описания типов файлов
        @Override
        public String getDescription() {
            return description;
        }
    }
}
