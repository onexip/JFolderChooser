package com.ultramixer.jfolderchooser.demo;

import com.ultramixer.jfolderchooser.FolderUtils;
import com.ultramixer.jfolderchooser.JFolderChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class UMFolderChooserDemo
{
    public static void main(String[] args)
    {

        FolderUtils.getInstance();

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFolderChooser.ConfigParameter config = new JFolderChooser.ConfigParameter();
                config.dialogTitle = "importiere Musik Verzeichnis";
                config.approveButtonText = "hinzufügen...";
                config.includeSubDirsTitle= "Include Subfolders";
                config.createGroupsTitle = "Create Separate Groups";
                config.recentVisible= true;

                JFolderChooser fc = JFolderChooser.getInstance(config);

                fc.removeAllActionsFromAddButton();
                fc.getAddButton().addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        System.out.println(fc.getSelectionFilePaths().size());
                    }
                });
            }
        });



    }
}
